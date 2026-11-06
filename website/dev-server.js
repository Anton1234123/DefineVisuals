"use strict";

// Local-only helper. Vercel uses the exported Express app from server.js and
// serves public/** from its CDN; this file is never imported by the Function.
const path = require("node:path");
const express = require("express");
const app = require("./server");

app.use(
  express.static(path.join(__dirname, "public"), {
    extensions: ["html"],
    fallthrough: true,
  })
);

const port = Number(process.env.PORT) || 3000;
const server = app.listen(port, () => {
  console.log(`Define Visuals site: http://localhost:${port}`);
  console.log("Static files are served by Express in local mode only.");
});

for (const signal of ["SIGINT", "SIGTERM"]) {
  process.on(signal, () => server.close(() => process.exit(0)));
}
