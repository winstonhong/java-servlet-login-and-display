#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
mkdir -p "$ROOT/certificates"
KEY="$ROOT/certificates/keystore.jks"
PASS="${KEYSTORE_PASS:-health}"
rm -f "$KEY"
keytool -genkeypair -alias jetty -keyalg RSA -keysize 2048 -storetype JKS \
  -keystore "$KEY" -storepass "$PASS" -keypass "$PASS" \
  -dname "CN=localhost, OU=Dev, O=Demo, L=City, ST=NA, C=US" -validity 3650
echo "Wrote $KEY (store/key password: $PASS, alias: jetty)"
