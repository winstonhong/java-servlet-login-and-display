# Dev TLS keystore

`jetty:run` expects **`keystore.jks`** here (password **`health`**, alias **`jetty`**) for HTTPS on **8443**.

Generate or regenerate:

```bash
./scripts/generate-dev-keystore.sh
```

This is a **self-signed** certificate for local OAuth redirects only. Use a real CA-signed certificate in production.
