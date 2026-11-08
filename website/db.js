"use strict";

const { Pool } = require("pg");

const SCHEMA = `
CREATE TABLE IF NOT EXISTS users (
  id TEXT PRIMARY KEY,
  username TEXT NOT NULL,
  version TEXT NOT NULL DEFAULT '',
  first_seen BIGINT NOT NULL,
  last_seen BIGINT NOT NULL,
  launches INTEGER NOT NULL DEFAULT 1
);
CREATE TABLE IF NOT EXISTS configs (
  id SERIAL PRIMARY KEY,
  owner_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  name TEXT NOT NULL,
  data TEXT NOT NULL,
  downloads INTEGER NOT NULL DEFAULT 0,
  created_at BIGINT NOT NULL
);
CREATE TABLE IF NOT EXISTS request_limits (
  bucket TEXT PRIMARY KEY,
  window_start BIGINT NOT NULL,
  hits INTEGER NOT NULL
);
CREATE INDEX IF NOT EXISTS configs_created_at_idx ON configs (created_at DESC);
CREATE INDEX IF NOT EXISTS configs_owner_id_idx ON configs (owner_id);
`;

/**
 * PostgreSQL-only storage.
 *
 * Vercel Functions are stateless and can be frozen or replaced at any moment,
 * so neither the users nor their configs may live in module variables or on
 * the local filesystem. Every read and write goes through DATABASE_URL.
 */
class PostgresStore {
  constructor(connectionString) {
    if (!connectionString) {
      throw new Error("DATABASE_URL is required; in-memory and filesystem storage are disabled");
    }

    const localDatabase = /(?:localhost|127\.0\.0\.1|\[::1\])/i.test(connectionString);
    this.pool = new Pool({
      connectionString,
      ssl: localDatabase ? false : { rejectUnauthorized: false },
      max: 3,
      idleTimeoutMillis: 10_000,
      connectionTimeoutMillis: 8_000,
      allowExitOnIdle: true,
      application_name: "definevisuals-vercel",
    });
    this.initialization = null;
    this.pool.on("error", (error) => console.error("[db] idle PostgreSQL client error:", error));
  }

  /** Creates the schema once per warm Function instance and retries after a failed attempt. */
  async init() {
    if (!this.initialization) {
      this.initialization = (async () => {
        try {
          await this.pool.query(SCHEMA);
        } catch (error) {
          // Several cold Function instances can run CREATE TABLE IF NOT EXISTS at
          // the same moment; PostgreSQL may reject one of the racing statements,
          // while the schema itself is already fine. One short retry is enough.
          await new Promise((resolve) => setTimeout(resolve, 250));
          await this.pool.query(SCHEMA);
        }
      })().catch((error) => {
        this.initialization = null;
        throw error;
      });
    }
    return this.initialization;
  }

  async upsertUser({ id, username, version }) {
    const now = Date.now();
    const result = await this.pool.query(
      `INSERT INTO users (id, username, version, first_seen, last_seen, launches)
       VALUES ($1, $2, $3, $4, $4, 1)
       ON CONFLICT (id) DO UPDATE
         SET username = EXCLUDED.username,
             version = EXCLUDED.version,
             last_seen = EXCLUDED.last_seen,
             launches = users.launches + 1
       RETURNING id, username, version, first_seen AS "firstSeen", last_seen AS "lastSeen", launches`,
      [id, username, version, now]
    );
    return result.rows[0];
  }

  async userExists(id) {
    const result = await this.pool.query("SELECT 1 FROM users WHERE id = $1", [id]);
    return result.rowCount > 0;
  }

  async stats() {
    const result = await this.pool.query(
      `SELECT
         (SELECT COUNT(*)::int FROM users) AS users,
         (SELECT COALESCE(SUM(launches), 0)::int FROM users) AS launches,
         (SELECT COUNT(*)::int FROM configs) AS configs,
         (SELECT COALESCE(SUM(downloads), 0)::int FROM configs) AS downloads`
    );
    return result.rows[0];
  }

  async listUsers() {
    const result = await this.pool.query(
      `SELECT username, version, first_seen AS "firstSeen", last_seen AS "lastSeen", launches
       FROM users
       ORDER BY last_seen DESC
       LIMIT 500`
    );
    return result.rows;
  }

  async listConfigs(ownerId) {
    if (ownerId) {
      const result = await this.pool.query(
        `SELECT c.id, c.name, c.downloads, c.created_at AS "createdAt", u.username AS owner,
                (c.owner_id = $1) AS "isMine"
         FROM configs c
         JOIN users u ON u.id = c.owner_id
         ORDER BY c.created_at DESC
         LIMIT 500`,
        [ownerId]
      );
      return result.rows;
    }

    const result = await this.pool.query(
      `SELECT c.id, c.name, c.downloads, c.created_at AS "createdAt", u.username AS owner
       FROM configs c
       JOIN users u ON u.id = c.owner_id
       ORDER BY c.created_at DESC
       LIMIT 500`
    );
    return result.rows;
  }

  /**
   * Uploads a config. Re-uploading the same name replaces the old row with a
   * fresh one (new id, reset download counter), exactly like the old API.
   * The whole check-and-replace runs in one transaction so a cold start or a
   * concurrent request cannot lose the existing config or exceed the limit.
   */
  async saveConfig({ ownerId, name, data, maxConfigs }) {
    const client = await this.pool.connect();
    try {
      await client.query("BEGIN");
      const owner = await client.query("SELECT 1 FROM users WHERE id = $1 FOR UPDATE", [ownerId]);
      if (owner.rowCount === 0) {
        const error = new Error("unknown_user");
        error.code = "UNKNOWN_USER";
        throw error;
      }

      const existing = await client.query(
        "SELECT id FROM configs WHERE owner_id = $1 AND lower(name) = lower($2)",
        [ownerId, name]
      );
      const count = await client.query("SELECT COUNT(*)::int AS c FROM configs WHERE owner_id = $1", [ownerId]);

      if (count.rows[0].c >= maxConfigs && existing.rowCount === 0) {
        const error = new Error("config_limit");
        error.code = "CONFIG_LIMIT";
        throw error;
      }

      const replaced = existing.rowCount > 0;
      if (replaced) {
        await client.query("DELETE FROM configs WHERE owner_id = $1 AND lower(name) = lower($2)", [ownerId, name]);
      }

      const inserted = await client.query(
        "INSERT INTO configs (owner_id, name, data, created_at) VALUES ($1, $2, $3, $4) RETURNING id",
        [ownerId, name, data, Date.now()]
      );
      await client.query("COMMIT");
      return { configId: inserted.rows[0].id, replaced };
    } catch (error) {
      try {
        await client.query("ROLLBACK");
      } catch (rollbackError) {
        console.error("[db] rollback failed:", rollbackError);
      }
      throw error;
    } finally {
      client.release();
    }
  }

  async updateConfig(id, ownerId, { name, data }) {
    const fields = [];
    const params = [id, ownerId];
    if (name != null) fields.push(`name = $${params.push(name)}`);
    if (data != null) fields.push(`data = $${params.push(data)}`);
    if (fields.length === 0) return false;

    const result = await this.pool.query(
      `UPDATE configs SET ${fields.join(", ")} WHERE id = $1 AND owner_id = $2`,
      params
    );
    return result.rowCount > 0;
  }

  async getConfig(id) {
    const result = await this.pool.query(
      `SELECT c.id, c.name, c.data, c.downloads, c.created_at AS "createdAt", u.username AS owner
       FROM configs c
       JOIN users u ON u.id = c.owner_id
       WHERE c.id = $1`,
      [id]
    );
    return result.rows[0] || null;
  }

  /** Increments and returns the fresh counter in one statement. */
  async incrementDownloads(id) {
    const result = await this.pool.query(
      "UPDATE configs SET downloads = downloads + 1 WHERE id = $1 RETURNING downloads",
      [id]
    );
    return result.rowCount > 0 ? result.rows[0].downloads : null;
  }

  async deleteConfig(id, ownerId) {
    const result = await this.pool.query("DELETE FROM configs WHERE id = $1 AND owner_id = $2", [id, ownerId]);
    return result.rowCount > 0;
  }

  /** Small, atomic, database-backed rate limiter (no per-instance RAM state). */
  async consumeRateLimit(bucket, limit, windowMs) {
    const now = Date.now();
    const resetBefore = now - windowMs;
    const result = await this.pool.query(
      `INSERT INTO request_limits (bucket, window_start, hits)
       VALUES ($1, $2, 1)
       ON CONFLICT (bucket) DO UPDATE
         SET hits = CASE WHEN request_limits.window_start < $3 THEN 1 ELSE request_limits.hits + 1 END,
             window_start = CASE WHEN request_limits.window_start < $3 THEN $2 ELSE request_limits.window_start END
       RETURNING hits`,
      [bucket, now, resetBefore]
    );

    if (Math.random() < 0.02) {
      this.pool
        .query("DELETE FROM request_limits WHERE window_start < $1", [now - 24 * 60 * 60 * 1000])
        .catch((error) => console.error("[db] rate-limit cleanup failed:", error));
    }
    return result.rows[0].hits <= limit;
  }

  async close() {
    await this.pool.end();
  }
}

function createStore(connectionString = process.env.DATABASE_URL) {
  return new PostgresStore(connectionString);
}

module.exports = { createStore, PostgresStore };
