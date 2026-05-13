#!/usr/bin/env bash
set -euo pipefail

IMAGE="${IMAGE:-idqlogindemo-jetty:latest}"
NAME="${CONTAINER_NAME:-idqlogindemo-jetty}"

# Default: bridge + publish 8080/8443 so "docker ps" shows PORTS, and JDBC reaches MySQL on the
# host via host.docker.internal (works with mysql/run.sh -p 3306:3306).
# Override: DOCKER_NETWORK=host (Linux only) — no -p; JDBC uses database.properties 127.0.0.1.
NET="${DOCKER_NETWORK:-bridge}"

docker rm -f "$NAME" 2>/dev/null || true

if [[ "$NET" == "host" ]]; then
  if [[ "$(uname -s)" != "Linux" ]]; then
    echo "Note: --network host is only fully supported on Linux. Otherwise use default bridge." >&2
  fi
  docker run --rm -d --name "$NAME" --network host "$IMAGE"
  echo "Started $NAME (--network host; no PORTS in docker ps). Logs: docker logs -f $NAME"
  exit 0
fi

DEFAULT_JDBC='jdbc:mysql://host.docker.internal:3306/COMPOSITEAPPS?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
JDBC_URL="${JDBC_URL:-$DEFAULT_JDBC}"

docker run --rm -d --name "$NAME" \
  -p 8080:8080 \
  -p 8443:8443 \
  --add-host=host.docker.internal:host-gateway \
  -e "JDBC_URL=${JDBC_URL}" \
  "$IMAGE"

echo "Started $NAME (bridge: 8080/tcp 8443/tcp → host). JDBC_URL uses host MySQL. Logs: docker logs -f $NAME"
