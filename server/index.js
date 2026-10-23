const http = require("node:http");
const { URL } = require("node:url");

const PORT = Number(process.env.PORT || 3000);
const API_KEY = process.env.QRAIL_API_KEY;
const ALLOWED_ORIGIN = process.env.ALLOWED_ORIGIN || "*";

function send(res, status, body) {
  res.writeHead(status, {
    "content-type": "application/json; charset=utf-8",
    "access-control-allow-origin": ALLOWED_ORIGIN,
    "access-control-allow-methods": "GET, OPTIONS",
    "access-control-allow-headers": "content-type"
  });
  res.end(JSON.stringify(body));
}

const server = http.createServer(async (req, res) => {
  if (req.method === "OPTIONS") {
    res.writeHead(204, {
      "access-control-allow-origin": ALLOWED_ORIGIN,
      "access-control-allow-methods": "GET, OPTIONS",
      "access-control-allow-headers": "content-type"
    });
    return res.end();
  }

  const url = new URL(req.url, "http://localhost");
  if (url.pathname === "/health" && req.method === "GET") {
    return send(res, 200, { ok: true, providerConfigured: Boolean(API_KEY) });
  }

  const match = url.pathname.match(/^\/api\/live\/([0-9]{5})$/);
  if (req.method !== "GET" || !match) {
    return send(res, 404, { error: "Not found" });
  }
  if (!API_KEY) {
    return send(res, 503, {
      error: "Railway API is not configured",
      help: "Set QRAIL_API_KEY in the server environment. Never put it in the Android app."
    });
  }

  try {
    const upstream = await fetch(
      "https://qrail.in/api/v3/trains/running/" + match[1] + "/today/" + encodeURIComponent(API_KEY),
      { headers: { accept: "application/json" }, signal: AbortSignal.timeout(12000) }
    );
    const bodyText = await upstream.text();
    let body;
    try { body = JSON.parse(bodyText); }
    catch { body = { error: "The railway provider returned an invalid response" }; }
    if (!upstream.ok) {
      return send(res, upstream.status === 429 ? 429 : 502, {
        error: "Railway provider request failed",
        providerStatus: upstream.status
      });
    }
    return send(res, 200, body);
  } catch (error) {
    return send(res, 502, { error: "Railway provider unavailable. Please try again later." });
  }
});

server.listen(PORT, "0.0.0.0", () => {
  console.log("My Train API proxy listening on port " + PORT);
});
