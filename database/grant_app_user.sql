-- Run as MySQL root *after* createtable_composite.sql.
-- Creates the account expected by LoginDao.java (mysqluser / mysqlpassword).

-- TCP clients often appear as 127.0.0.1 or a Docker bridge IP; @'localhost' alone can deny them.
CREATE USER IF NOT EXISTS 'mysqluser'@'localhost' IDENTIFIED BY 'mysqlpassword';
GRANT ALL PRIVILEGES ON COMPOSITEAPPS.* TO 'mysqluser'@'localhost';

CREATE USER IF NOT EXISTS 'mysqluser'@'127.0.0.1' IDENTIFIED BY 'mysqlpassword';
GRANT ALL PRIVILEGES ON COMPOSITEAPPS.* TO 'mysqluser'@'127.0.0.1';

CREATE USER IF NOT EXISTS 'mysqluser'@'%' IDENTIFIED BY 'mysqlpassword';
GRANT ALL PRIVILEGES ON COMPOSITEAPPS.* TO 'mysqluser'@'%';

FLUSH PRIVILEGES;

-- If you use MySQL 8+ and the app still cannot authenticate (not "Communications link failure"),
-- the old JDBC driver may need mysql_native_password for this user:
-- ALTER USER 'mysqluser'@'localhost' IDENTIFIED WITH mysql_native_password BY 'mysqlpassword';
-- FLUSH PRIVILEGES;
