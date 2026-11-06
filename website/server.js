"use strict";

const crypto = require("node:crypto");
const fs = require("node:fs");
const path = require("node:path");
const express = require("express");
const { createStore } = require("./db");

const ID_PATTERN = /^DV-[A-Z0-9]{4}(-[A-Z0-9]{4}){3}$/;
const MAX_CONFIGS_PER_USER = 20;
const MAX_CONFIG_SIZE = 512 * 1024;
const MAX_CONFIG_NAME = 48;
const MAX_USERNAME = 24;

const PING_RATE = { limit: 30, windowMs: 60_000 };
const UPLOAD_RATE = { limit: 10, windowMs: 60_000 };
const RATE_LIMIT_SALT = process.env.RATE_LIMIT_SALT || "definevisuals-rate-limit-v1";

let defaultStore = null;

function resolveDefaultStore() {
  if (!defaultStore) {
    defaultStore = createStore();
  }
  return defaultStore;
}

function cleanUsername(value) {
  if (typeof value !== "string") return null;
  const trimmed = value.trim();
  if (trimmed.length < 1 || trimmed.length > MAX_USERNAME) return null;
  if (!/^[\p{L}\p{N}_.!?\- ]+$/u.test(trimmed)) return null;
  return trimmed;
}

function cleanConfigName(value) {
  if (typeof value !== "string") return null;
  const trimmed = value.trim().replace(/[\x00-\x1F<>:"/\\|?*]/g, "");
  if (trimmed.length < 1 || trimmed.length > MAX_CONFIG_NAME) return null;
  return trimmed;
}

function isConfigData(value) {
  if (typeof value !== "string" || value.length === 0) return false;
  if (Buffer.byteLength(value, "utf8") > MAX_CONFIG_SIZE) return false;
  try {
    const parsed = JSON.parse(value);
    return Boolean(parsed) && Array.isArray(parsed.modules);
  } catch {
    return false;
  }
}

function badRequest(res, error) {
  return res.status(400).json({ ok: false, error });
}

function clientFingerprint(req) {
  const realIp = req.headers["x-real-ip"];
  const forwarded = req.headers["x-forwarded-for"];
  const raw = Array.isArray(forwarded) ? forwarded[0] : forwarded;
  const first = typeof raw === "string" ? raw.split(",")[0].trim() : "";
  const address =
    (typeof realIp === "string" && realIp.trim()) || first || req.ip || "unknown";
  return crypto.createHash("sha256").update(`${RATE_LIMIT_SALT}:${address}`).digest("hex").slice(0, 32);
}

async function isRateLimited(req, res, name, settings) {
  const key = `${name}:${clientFingerprint(req)}`;
  const allowed = await req.db.consumeRateLimit(key, settings.limit, settings.windowMs);
  if (!allowed) {
    res.status(429).json({ ok: false, error: "too_many_requests" });
    return true;
  }
  return false;
}

function createApp(options = {}) {
  const app = express();
  const injectedStore = options.store || null;

  app.disable("x-powered-by");
  app.set("trust proxy", true);
  app.use(express.json({ limit: "2mb" }));

  app.use((req, res, next) => {
    res.setHeader("X-Content-Type-Options", "nosniff");
    res.setHeader("Referrer-Policy", "no-referrer");
    res.setHeader("X-Frame-Options", "DENY");
    res.setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=()");
    if (req.path.startsWith("/api")) {
      res.setHeader("Cache-Control", "no-store");
    }
    next();
  });

  // Every API request runs against PostgreSQL. The schema promise is reused by
  // a warm Vercel Function, but user/config data itself is never cached in RAM.
  app.use("/api", async (req, res, next) => {
    try {
      req.db = injectedStore || resolveDefaultStore();
      await req.db.init();
      next();
    } catch (error) {
      console.error("[db] initialization failed:", error.message || error);
      res.status(503).json({ ok: false, error: "database_unavailable" });
    }
  });

  app.get("/api/health", (req, res) => {
    res.json({ ok: true, storage: "postgresql" });
  });

  app.post("/api/ping", async (req, res) => {
    const { id, username, version } = req.body || {};
    if (typeof id !== "string" || !ID_PATTERN.test(id)) return badRequest(res, "invalid_id");

    const name = cleanUsername(username);
    if (!name) return badRequest(res, "invalid_username");
    const clientVersion = typeof version === "string" ? version.trim().slice(0, 64) : "";

    try {
      if (await isRateLimited(req, res, "ping", PING_RATE)) return;

      await req.db.upsertUser({ id, username: name, version: clientVersion });
      const stats = await req.db.stats();
      res.json({ ok: true, users: stats.users });
    } catch (error) {
      console.error("[api] ping error:", error);
      res.status(500).json({ ok: false, error: "internal" });
    }
  });

  app.get("/api/stats", async (req, res) => {
    try {
      res.json({ ok: true, ...(await req.db.stats()) });
    } catch (error) {
      console.error("[api] stats error:", error);
      res.status(500).json({ ok: false, error: "internal" });
    }
  });

  app.get("/api/users", async (req, res) => {
    try {
      res.json({ ok: true, users: await req.db.listUsers() });
    } catch (error) {
      console.error("[api] users error:", error);
      res.status(500).json({ ok: false, error: "internal" });
    }
  });

  app.get("/api/configs", async (req, res) => {
    // Like the previous backend: an unknown/invalid id just means "no isMine".
    const ownerId =
      typeof req.query.id === "string" && ID_PATTERN.test(req.query.id) ? req.query.id : null;

    try {
      res.json({ ok: true, configs: await req.db.listConfigs(ownerId) });
    } catch (error) {
      console.error("[api] configs error:", error);
      res.status(500).json({ ok: false, error: "internal" });
    }
  });

  app.post("/api/configs", async (req, res) => {
    const { id, name, data } = req.body || {};
    if (typeof id !== "string" || !ID_PATTERN.test(id)) return badRequest(res, "invalid_id");

    const cleanName = cleanConfigName(name);
    if (!cleanName) return badRequest(res, "invalid_name");
    if (typeof data !== "string" || data.length === 0) return badRequest(res, "invalid_data");
    if (Buffer.byteLength(data, "utf8") > MAX_CONFIG_SIZE) return badRequest(res, "config_too_large");
    if (!isConfigData(data)) return badRequest(res, "not_a_config");

    try {
      if (await isRateLimited(req, res, "upload", UPLOAD_RATE)) return;
      if (!(await req.db.userExists(id))) return badRequest(res, "unknown_user");

      const saved = await req.db.saveConfig({
        ownerId: id,
        name: cleanName,
        data,
        maxConfigs: MAX_CONFIGS_PER_USER,
      });
      res.json({ ok: true, configId: saved.configId, replaced: saved.replaced });
    } catch (error) {
      if (error.code === "CONFIG_LIMIT") {
        return res.status(403).json({ ok: false, error: "config_limit", limit: MAX_CONFIGS_PER_USER });
      }
      if (error.code === "UNKNOWN_USER") return badRequest(res, "unknown_user");
      console.error("[api] upload error:", error);
      res.status(500).json({ ok: false, error: "internal" });
    }
  });

  app.get("/api/configs/:id/download", async (req, res) => {
    const configId = Number.parseInt(req.params.id, 10);
    if (!/^\d+$/.test(req.params.id) || !Number.isSafeInteger(configId)) {
      return badRequest(res, "invalid_id");
    }

    try {
      const config = await req.db.getConfig(configId);
      if (!config) return res.status(404).json({ ok: false, error: "not_found" });

      const downloads = await req.db.incrementDownloads(configId);
      if (req.query.file === "1") {
        const fallback = config.name.replace(/[^\x20-\x7E]/g, "_").replace(/["\\]/g, "_").slice(0, 80) || "config";
        const encoded = encodeURIComponent(`${config.name}.def`).replace(
          /['()*]/g,
          (character) => `%${character.charCodeAt(0).toString(16).toUpperCase()}`
        );
        res.setHeader("Content-Type", "application/json; charset=utf-8");
        res.setHeader(
          "Content-Disposition",
          `attachment; filename="${fallback}.def"; filename*=UTF-8''${encoded}`
        );
        return res.send(config.data);
      }

      res.json({
        ok: true,
        name: config.name,
        owner: config.owner,
        downloads: downloads ?? config.downloads + 1,
        data: config.data,
      });
    } catch (error) {
      console.error("[api] download error:", error);
      res.status(500).json({ ok: false, error: "internal" });
    }
  });

  app.put("/api/configs/:id", async (req, res) => {
    const configId = Number.parseInt(req.params.id, 10);
    if (!/^\d+$/.test(req.params.id) || !Number.isSafeInteger(configId)) {
      return badRequest(res, "invalid_id");
    }

    const { id: ownerId, name, data } = req.body || {};
    if (typeof ownerId !== "string" || !ID_PATTERN.test(ownerId)) return badRequest(res, "invalid_owner");

    let cleanName = null;
    if (name !== undefined) {
      cleanName = cleanConfigName(name);
      if (!cleanName) return badRequest(res, "invalid_name");
    }
    if (data !== undefined) {
      if (typeof data !== "string" || data.length === 0) return badRequest(res, "invalid_data");
      if (Buffer.byteLength(data, "utf8") > MAX_CONFIG_SIZE) return badRequest(res, "config_too_large");
      if (!isConfigData(data)) return badRequest(res, "not_a_config");
    }
    if (cleanName === null && data === undefined) return badRequest(res, "nothing_to_update");

    try {
      const updated = await req.db.updateConfig(configId, ownerId, { name: cleanName, data });
      if (!updated) return res.status(404).json({ ok: false, error: "not_found" });
      res.json({ ok: true });
    } catch (error) {
      console.error("[api] update error:", error);
      res.status(500).json({ ok: false, error: "internal" });
    }
  });

  app.delete("/api/configs/:id", async (req, res) => {
    const configId = Number.parseInt(req.params.id, 10);
    if (!/^\d+$/.test(req.params.id) || !Number.isSafeInteger(configId)) {
      return badRequest(res, "invalid_id");
    }

    const { id: ownerId } = req.body || {};
    if (typeof ownerId !== "string" || !ID_PATTERN.test(ownerId)) return badRequest(res, "invalid_owner");

    try {
      const deleted = await req.db.deleteConfig(configId, ownerId);
      if (!deleted) return res.status(404).json({ ok: false, error: "not_found" });
      res.json({ ok: true });
    } catch (error) {
      console.error("[api] delete error:", error);
      res.status(500).json({ ok: false, error: "internal" });
    }
  });

  app.use("/api", (req, res) => {
    res.status(404).json({ ok: false, error: "not_found" });
  });

  // Vercel serves public/** from its CDN, but the Express preset routes "/" to
  // this Function. Answer it explicitly; fall back to the CDN copy if the
  // static file was not bundled into the Function.
  app.get("/", (req, res) => {
    const indexFile = path.join(__dirname, "public", "index.html");
    if (fs.existsSync(indexFile)) {
      return res.sendFile(indexFile);
    }
    return res.redirect(302, "/index.html");
  });

  app.use((error, req, res, next) => {
    if (res.headersSent) return next(error);
    if (error && error.type === "entity.too.large") {
      return res.status(413).json({ ok: false, error: "payload_too_large" });
    }
    if (error instanceof SyntaxError && error.status === 400 && "body" in error) {
      return res.status(400).json({ ok: false, error: "invalid_json" });
    }
    console.error("[api] unhandled Express error:", error);
    res.status(500).json({ ok: false, error: "internal" });
  });

  return app;
}

// Vercel imports this Express application and owns the HTTP listener; there is
// deliberately no app.listen() in the serverless entrypoint.
const app = createApp();
module.exports = app;
module.exports.createApp = createApp;
