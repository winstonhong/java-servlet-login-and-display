#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
IMAGE="${IMAGE:-idqlogindemo-jetty:latest}"

docker build -f "${ROOT}/Docker/Dockerfile" -t "$IMAGE" "$@" "$ROOT"
echo "Image: $IMAGE"
