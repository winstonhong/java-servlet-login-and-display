Java Servlet Login and Display
--------------------

Most web apps start with system Login and data Display.

This simple app serves both purposes by Building backend with MySQL 5 for simple Login system and Displaying responsive Data Table with Bootstrap 3. The WAR targets **Servlet 3 / Tomcat 8**-style containers. Local development uses **Jetty 9** with **HTTP on 8080** and **HTTPS on 8443** by default (OAuth-friendly). Optional **Cargo** profile runs embedded **Tomcat 8.5** if you need that stack locally.

A user can log in to this simple web application using username/password credential or through a third-party authentication provider such as [idQ Trust as a Service (idQ TaaS)](https://www.inbaytech.com/) or [Google Identity Platform](https://developers.google.com/identity/) via [OAuth 2.0 protocol](https://oauth.net/2/).  

![Screenshot](screenshot/login.png)
![Screenshot](screenshot/display.png)


Installation
------------
+ **MySQL must be running** on the same machine as the app (the code uses `jdbc:mysql://localhost/...` on the default port `3306`). If you see `CommunicationsException` / "Communications link failure", the server is not listening.
+ **Option A — Docker:** From the project root, `./mysql/build.sh` then `./mysql/run.sh`. This builds image `mysql:latest`, starts container `mysql`, maps **3306**, creates user **`mysqluser` / `mysqlpassword`** and loads `database/createtable_composite.sql` on first start. Override root password with `MYSQL_ROOT_PASSWORD=... ./mysql/run.sh` if needed. Reset data: `docker rm -f mysql` and `docker volume rm mysql-data`, then run `./mysql/run.sh` again. The app uses **MySQL Connector/J 8.x** so it works with **MySQL 8** from `mysql:latest` (the old 5.1 driver often fails with “Could not create connection to database server”).
+ **Option B — Native:** Install and start MySQL or MariaDB (for example on Debian/Ubuntu: `sudo apt install mariadb-server`, then `sudo systemctl start mariadb`, and check with `ss -tlnp | grep 3306`). Run `database/createtable_composite.sql` as an admin user, then run `database/grant_app_user.sql` so the `mysqluser` account exists (or change credentials in **`src/main/resources/database.properties`**).
+ JDBC URL and credentials default from **`src/main/resources/database.properties`** (override host/port/user/password there if needed).
+ Input OAuth 2.0 authentication credential in 'src/main/resources/Oauth2.properties'.
+ **Run locally (recommended):** **`mvn clean package jetty:run`** — **HTTP** on **`0.0.0.0:8080`** and **HTTPS** on **`0.0.0.0:8443`** (see **`jetty/dev-server.xml`**), context **`/`**. Ensure **`certificates/keystore.jks`** exists (password **`health`**); create it with **`./scripts/generate-dev-keystore.sh`**. Wrapper: **`./scripts/dev-server.sh`**. Override ports: **`-Dhttp.port=9090 -Dhttps.port=9443`**. Self-signed cert: browsers show a warning; use **Advanced → proceed** or **`curl -k https://HOST:8443/`**.
+ **Embedded Tomcat 8.5 (optional):** **`mvn clean package -Ptomcat8-cargo cargo:run`** or **`./scripts/cargo-dev.sh`**. Cargo’s embedded Tomcat has had **root-context / 404** quirks for this project; use Jetty for day-to-day dev unless you specifically need Tomcat. **Tomcat HTTPS only:** **`mvn clean package -Ptomcat8-cargo,https cargo:run`**.
+ **Ports 8080 / 8443 in use:** Run **`ss -tlnp | grep -E '8080|8443'`**, **`kill <pid>`** on the **`java`** process, or change **`http.port`** / **`https.port`** as above.
+ **App in Docker (Jetty):** **`Docker/build.sh`** then **`Docker/run.sh`** runs **`mvn clean package jetty:run`** in the container. **Default:** bridge networking with **`-p 8080:8080 -p 8443:8443`** (so **`docker ps` shows PORTS) and **`JDBC_URL=...host.docker.internal:3306...`** so the app reaches **MySQL on the host** (e.g. **`mysql/run.sh`** publishing **3306**). **Linux optional:** **`DOCKER_NETWORK=host Docker/run.sh`** uses the host network (no PORTS column); JDBC then follows **`database.properties`**. If login fails in Docker, check **`docker logs`** for **`LoginDao`** messages and ensure MySQL allows **`mysqluser`** from your client host (see **`database/grant_app_user.sql`**). Stop: **`docker rm -f idqlogindemo-jetty`**.


Docker Deployment
------------
```bash
git clone https://github.com/winstonhong/java-servlet-login-and-display
cd java-servlet-login-and-display
# Deploy MySQL Docker container
bash mysql/build.sh
bash mysql/run.sh
# Deploy Web App Docker container
bash Docker/build.sh
bash Docker/run.sh
```

Demo
------------
+ **OAuth demo:** Open **`https://YOUR-IP:8443/`**.
+ **Same machine:** You can use **`https://localhost:8443/`**.
+ **Non-OAuth testing:** Plain **HTTP** is still available at **`http://YOUR-IP:8080/`**.
+ **VMware:** Use the **VM IP address**, not the host machine’s **`localhost`**.


OAuth 2.0 Login Demo
------------
+ Use **`https://localhost:8443/`** (or **`https://YOUR-HOSTNAME:8443/`**) so the redirect URI matches **`Oauth2.properties`** and your OAuth provider’s registered callback (see **`redirectUrl`** there).
+ Click idQ Sign In
+ You are redirected to idQ TaaS login screen
+ Scan idQ QR code using your idQ Trusted Device
+ You will be redirected back to reach the home screen of employee information if you have been authenticated by idQ TaaS successfully via OAuth 2.0 protocol.

Version History
---------------

v0.12:


- Fixed closing all database connections after each session use or logout.

v1.01:


- Add OAuth 2.0 login support.


Credit
-------
Basic interview project by http://www.compositeapps.net/

Support
-------
Originally Developed by http://ntt2k.io

OAuth 2.0 support Developed by [winstonhong](https://github.com/winstonhong) @ [inbaytech](https://github.com/inbaytech)
