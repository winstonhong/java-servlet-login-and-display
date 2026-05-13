FROM mysql:latest

# Runs once on first container start (empty data directory).
# Lexicographic order if multiple files are added later.
COPY database/createtable_composite.sql /docker-entrypoint-initdb.d/01-composite.sql
