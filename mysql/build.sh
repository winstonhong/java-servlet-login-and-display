#!/usr/bin/env bash
set -euo pipefail

# Build context must be the project root so COPY database/... works.
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

docker build -f mysql/Dockerfile -t mysql:latest .
