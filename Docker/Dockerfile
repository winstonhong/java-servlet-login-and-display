# Jetty dev server (mvn jetty:run) with HTTP 8080 + HTTPS 8443 — same as local.
# Build context: project root (see build.sh).

FROM maven:3.9-eclipse-temurin-8

WORKDIR /app

COPY pom.xml .
COPY jetty jetty/
COPY scripts scripts/

RUN mvn -B -q dependency:go-offline -DskipTests || true

COPY src src/

RUN mkdir -p certificates \
    && keytool -genkeypair -alias jetty -keyalg RSA -keysize 2048 -storetype JKS \
        -keystore certificates/keystore.jks -storepass health -keypass health \
        -dname "CN=localhost, OU=Dev, O=Demo, L=City, ST=NA, C=US" -validity 3650

EXPOSE 8080 8443

CMD ["mvn", "-B", "clean", "package", "jetty:run", "-DskipTests"]
