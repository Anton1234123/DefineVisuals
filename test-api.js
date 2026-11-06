"use strict";

// Smoke test for the HTTP contract without touching production PostgreSQL.
// The production server always uses PostgreSQL through DATABASE_URL; the
// in-memory store below exists only inside this test process.
const fs = require("node:fs");
const path = require("node:path");
const { createApp } = require("./server");
const { createStore } = require("./db");

class MemoryStore {
  constructor() {
    this.users = new Map();
    this.configs = [];
    this.nextConfigId = 1;
  }

  async init() {}

  async upsertUser({ id, username, version }) {
    const now = Date.now();
    const existing = this.users.get(id);
    const user = existing
      ? { ...existing, username, version, lastSeen: now, launches: existing.launches + 1 }
      : { id, username, version, firstSeen: now, lastSeen: now, launches: 1 };
    this.users.set(id, user);
    return user;
  }

  async userExists(id) {
    return this.users.has(id);
  }

  async stats() {
    const users = [...this.users.values()];
    return {
      users: users.length,
      launches: users.reduce((sum, user) => sum + user.launches, 0),
      configs: this.configs.length,
      downloads: this.configs.reduce((sum, config) => sum + config.downloads, 0),
    };
  }

  async listUsers() {
    return [...this.users.values()]
      .sort((left, right) => right.lastSeen - left.lastSeen)
      .map(({ username, version, firstSeen, lastSeen, launches }) => ({
        username,
        version,
        firstSeen,
        lastSeen,
        launches,
      }));
  }

  async listConfigs(ownerId) {
    return [...this.configs]
      .sort((left, right) => right.createdAt - left.createdAt)
      .map((config) => {
        const row = {
          id: config.id,
          name: config.name,
          downloads: config.downloads,
          createdAt: config.createdAt,
          owner: this.users.get(config.ownerId).username,
        };
        if (ownerId) row.isMine = config.ownerId === ownerId;
        return row;
      });
  }

  async saveConfig({ ownerId, name, data, maxConfigs }) {
    if (!this.users.has(ownerId)) {
      const error = new Error("unknown_user");
      error.code = "UNKNOWN_USER";
      throw error;
    }

    const ownConfigs = this.configs.filter((config) => config.ownerId === ownerId);
    const existing = ownConfigs.filter((config) => config.name.toLowerCase() === name.toLowerCase());
    if (ownConfigs.length >= maxConfigs && existing.length === 0) {
      const error = new Error("config_limit");
      error.code = "CONFIG_LIMIT";
      throw error;
    }

    this.configs = this.configs.filter(
      (config) => !(config.ownerId === ownerId && config.name.toLowerCase() === name.toLowerCase())
    );
    const config = { id: this.nextConfigId++, ownerId, name, data, downloads: 0, createdAt: Date.now() };
    this.configs.push(config);
    return { configId: config.id, replaced: existing.length > 0 };
  }

  async updateConfig(id, ownerId, { name, data }) {
    const config = this.configs.find((entry) => entry.id === id && entry.ownerId === ownerId);
    if (!config) return false;
    if (name != null) config.name = name;
    if (data != null) config.data = data;
    return true;
  }

  async getConfig(id) {
    const config = this.configs.find((entry) => entry.id === id);
    if (!config) return null;
    return { ...config, owner: this.users.get(config.ownerId).username };
  }

  async incrementDownloads(id) {
    const config = this.configs.find((entry) => entry.id === id);
    if (!config) return null;
    config.downloads += 1;
    return config.downloads;
  }

  async deleteConfig(id, ownerId) {
    const index = this.configs.findIndex((entry) => entry.id === id && entry.ownerId === ownerId);
    if (index === -1) return false;
    this.configs.splice(index, 1);
    return true;
  }

  async consumeRateLimit() {
    return true;
  }
}

const configData = JSON.stringify({ modules: [{ name: "Zoom", enabled: true, key: -1, settings: {} }] });
const configDataV2 = JSON.stringify({
  modules: [
    { name: "Zoom", enabled: true, key: -1, settings: {} },
    { name: "Fullbright", enabled: true, key: -1, settings: {} },
  ],
});

(async () => {
  let fails = 0;
  const check = (name, condition) => {
    console.log((condition ? "PASS" : "FAIL") + " | " + name);
    if (!condition) fails += 1;
  };

  check("DATABASE_URL is mandatory in production", (() => {
    const saved = process.env.DATABASE_URL;
    delete process.env.DATABASE_URL;
    try {
      createStore();
      return false;
    } catch (error) {
      return /DATABASE_URL is required/.test(error.message);
    } finally {
      if (saved !== undefined) process.env.DATABASE_URL = saved;
    }
  })());

  const app = createApp({ store: new MemoryStore() });
  const server = app.listen(0, "127.0.0.1");
  await new Promise((resolve) => server.once("listening", resolve));
  const base = `http://127.0.0.1:${server.address().port}`;

  async function req(urlPath, body, method) {
    const response = await fetch(base + urlPath, {
      method: method || (body ? "POST" : "GET"),
      headers: { "Content-Type": "application/json" },
      body: body ? JSON.stringify(body) : undefined,
    });
    return { status: response.status, json: await response.json().catch(() => null) };
  }

  async function del(urlPath, body) {
    const response = await fetch(base + urlPath, {
      method: "DELETE",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
    return { status: response.status, json: await response.json().catch(() => null) };
  }

  // 1-4. User registration and validation
  let result = await req("/api/ping", { id: "DV-TEST-1234-ABCD-EFGH", username: "Steve", version: "1.1 Alpha" });
  check("ping new user", result.status === 200 && result.json.ok && result.json.users === 1);

  result = await req("/api/ping", { id: "DV-TEST-1234-ABCD-EFGH", username: "Steve", version: "1.1 Alpha" });
  check("ping again increments launches", result.status === 200 && result.json.ok && result.json.users === 1);

  result = await req("/api/ping", { id: "DV-X9K2-M3Q7-1XAB-C4D5", username: "Алекс_2007!", version: "1.1 Alpha" });
  check("ping second user (cyrillic nick with punctuation)", result.status === 200 && result.json.users === 2);

  result = await req("/api/ping", { id: "hacked", username: "x" });
  check("invalid id rejected", result.status === 400);

  // 5. Public users list
  result = await req("/api/users");
  const noIds = result.json.users.every((user) => !("id" in user));
  check("users list has no IDs", result.status === 200 && result.json.users.length === 2 && noIds);
  check("launch counter", result.json.users.find((user) => user.username === "Steve").launches === 2);

  // 6-8b. Uploading and replacing configs
  result = await req("/api/configs", { id: "DV-TEST-1234-ABCD-EFGH", name: "PVP Конфиг", data: configData });
  let configId = result.json.configId;
  check("upload config", result.status === 200 && result.json.ok && configId === 1);

  result = await req("/api/configs", { id: "DV-ZZZZ-ZZZZ-ZZZZ-ZZZZ", name: "x", data: configData });
  check("unknown user upload rejected", result.status === 400 && result.json.error === "unknown_user");

  result = await req("/api/configs", { id: "DV-TEST-1234-ABCD-EFGH", name: "x", data: "hello world" });
  check("garbage config rejected", result.status === 400);

  result = await req("/api/configs", { id: "DV-TEST-1234-ABCD-EFGH", name: "PVP Конфиг", data: configDataV2 });
  check("reupload replaces by name", result.status === 200 && result.json.ok && result.json.replaced === true && result.json.configId !== configId);
  configId = result.json.configId;
  result = await req("/api/configs");
  check("replace kept single entry", result.json.configs.length === 1);

  // 9. Catalog and isMine
  result = await req("/api/configs");
  check("configs list", result.status === 200 && result.json.configs.length === 1 && result.json.configs[0].name === "PVP Конфиг" && result.json.configs[0].owner === "Steve");
  check("configs list has no data field", !("data" in result.json.configs[0]));

  result = await req("/api/configs?id=DV-TEST-1234-ABCD-EFGH");
  check("isMine true for owner", result.status === 200 && result.json.configs[0].isMine === true);
  result = await req("/api/configs?id=DV-X9K2-M3Q7-1XAB-C4D5");
  check("isMine false for foreign", result.status === 200 && result.json.configs[0].isMine === false);
  result = await req("/api/configs");
  check("isMine absent without id", !("isMine" in result.json.configs[0]));
  result = await req("/api/configs?id=not-an-id");
  check("invalid catalog id ignored", result.status === 200 && !("isMine" in result.json.configs[0]));

  // 9c. PUT update
  result = await req(`/api/configs/${configId}`, { id: "DV-X9K2-M3Q7-1XAB-C4D5", data: configData }, "PUT");
  check("foreign PUT rejected", result.status === 404);
  result = await req(`/api/configs/${configId}`, { id: "DV-TEST-1234-ABCD-EFGH", name: "PVP Updated", data: configDataV2 }, "PUT");
  check("owner PUT update", result.status === 200 && result.json.ok);
  result = await req(`/api/configs/${configId}`, { id: "DV-TEST-1234-ABCD-EFGH" }, "PUT");
  check("empty PUT rejected", result.status === 400);

  // 10-11. Download and file attachment
  result = await req(`/api/configs/${configId}/download`);
  check("download config updated data", result.status === 200 && result.json.data === configDataV2 && result.json.name === "PVP Updated");
  result = await req("/api/configs");
  check("download counter", result.json.configs[0].downloads === 1);

  const fileResponse = await fetch(base + `/api/configs/${configId}/download?file=1`);
  const disposition = fileResponse.headers.get("content-disposition") || "";
  check("file download", fileResponse.status === 200 && disposition.includes(".def") && (await fileResponse.text()) === configDataV2);

  // 12. Stats and health
  result = await req("/api/stats");
  check("stats", result.status === 200 && result.json.users === 2 && result.json.launches === 3 && result.json.configs === 1 && result.json.downloads === 2);
  result = await req("/api/health");
  check("health reports PostgreSQL", result.status === 200 && result.json.ok && result.json.storage === "postgresql");

  // 12b. Config size limits (the HTTP body limit must stay above the config limit)
  result = await req("/api/ping", { id: "DV-BIG0-0000-0000-0000", username: "SizeTester", version: "1.1 Alpha" });
  check("size test user registered", result.status === 200 && result.json.ok);
  const largeValid = JSON.stringify({ modules: [{ name: "Big", settings: { blob: "x".repeat(400 * 1024) } }] });
  result = await req("/api/configs", { id: "DV-BIG0-0000-0000-0000", name: "Big Config", data: largeValid });
  check("large valid config accepted", result.status === 200 && result.json.ok);
  const tooLarge = JSON.stringify({ modules: [{ name: "TooBig", settings: { blob: "x".repeat(530 * 1024) } }] });
  result = await req("/api/configs", { id: "DV-BIG0-0000-0000-0000", name: "Too Large", data: tooLarge });
  check("oversized config rejected", result.status === 400 && result.json.error === "config_too_large");

  // 13-14. Deleting someone else's config is forbidden
  result = await del(`/api/configs/${configId}`, { id: "DV-X9K2-M3Q7-1XAB-C4D5" });
  check("foreign delete rejected", result.status === 404);
  result = await del(`/api/configs/${configId}`, { id: "DV-TEST-1234-ABCD-EFGH" });
  check("owner delete", result.status === 200 && result.json.ok);

  // 15. Static site files
  const index = fs.readFileSync(path.join(__dirname, "public", "index.html"), "utf8");
  const privacy = fs.readFileSync(path.join(__dirname, "public", "privacy.html"), "utf8");
  check("index page", index.includes("Define Visuals"));
  check("privacy page", privacy.includes("Privacy Policy"));

  // 16. Malformed JSON produces a JSON error, not an HTML crash page
  const malformed = await fetch(base + "/api/ping", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: "{broken",
  });
  const malformedJson = await malformed.json().catch(() => null);
  check("malformed JSON handled", malformed.status === 400 && malformedJson && malformedJson.error === "invalid_json");

  server.close();
  console.log(fails === 0 ? "\nALL TESTS PASSED" : `\n${fails} FAILURES`);
  process.exit(fails === 0 ? 0 : 1);
})();
