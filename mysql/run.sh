#!/usr/bin/env bash
set -euo pipefail

# Match LoginDao.java: jdbc:mysql://localhost/COMPOSITEAPPS, mysqluser / mysqlpassword
MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD:-rootsecret}"
MYSQL_DATABASE="${MYSQL_DATABASE:-COMPOSITEAPPS}"
MYSQL_USER="${MYSQL_USER:-mysqluser}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-mysqlpassword}"

NAME="${MYSQL_CONTAINER_NAME:-mysql}"

if docker container inspect "$NAME" &>/dev/null; then
  echo "Container '$NAME' already exists. Remove it first: docker rm -f $NAME" >&2
  exit 1
fi

docker run -d \
  --name "$NAME" \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD="$MYSQL_ROOT_PASSWORD" \
  -e MYSQL_DATABASE="$MYSQL_DATABASE" \
  -e MYSQL_USER="$MYSQL_USER" \
  -e MYSQL_PASSWORD="$MYSQL_PASSWORD" \
  -v mysql-data:/var/lib/mysql \
  mysql:latest

echo "MySQL container '$NAME' started. JDBC from host: jdbc:mysql://127.0.0.1:3306/${MYSQL_DATABASE}"
echo "First-time init can take a minute (large seed SQL). Check logs: docker logs -f $NAME"
