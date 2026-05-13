#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

HTTP_PORT="${HTTP_PORT:-8080}"
HTTPS_PORT="${HTTPS_PORT:-8443}"
for arg in "$@"; do
  case "$arg" in
    -Dhttp.port=*) HTTP_PORT="${arg#-Dhttp.port=}" ;;
    -Dhttps.port=*) HTTPS_PORT="${arg#-Dhttps.port=}" ;;
  esac
done

busy=0
for p in "$HTTP_PORT" "$HTTPS_PORT"; do
  if ss -tln 2>/dev/null | grep -qE ":${p}[[:space:]]"; then
    echo "Port ${p} is already in use." >&2
    ss -tlnp 2>/dev/null | grep -E ":${p}[[:space:]]" || true
    busy=1
  fi
done
if [[ "$busy" -ne 0 ]]; then
  echo "" >&2
  echo "Free the ports or override, e.g.:" >&2
  echo "  kill <pid>" >&2
  echo "  HTTP_PORT=9090 HTTPS_PORT=9443 $0" >&2
  echo "  $0 -Dhttp.port=9090 -Dhttps.port=9443" >&2
  exit 1
fi

exec mvn clean package jetty:run "$@"
