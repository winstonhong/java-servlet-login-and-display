#!/usr/bin/env bash
set -euo pipefail
# Embedded Tomcat 8 via Cargo (optional). Prefer ./scripts/dev-server.sh (Jetty) for reliable root URL /.

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

PORT="${HTTP_PORT:-8080}"
for arg in "$@"; do
  case "$arg" in
    -Dhttp.port=*) PORT="${arg#-Dhttp.port=}" ;;
  esac
done

if ss -tln 2>/dev/null | grep -qE ":${PORT}[[:space:]]"; then
  echo "Port ${PORT} is already in use." >&2
  ss -tlnp 2>/dev/null | grep -E ":${PORT}[[:space:]]" || true
  exit 1
fi

exec mvn clean package -Ptomcat8-cargo cargo:run "$@"
