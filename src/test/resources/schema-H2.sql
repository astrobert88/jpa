-- This schema is H2 syntax specific
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS users_roles;
DROP TABLE IF EXISTS hibernate_sequence;
DROP SEQUENCE IF EXISTS HIBERNATE_SEQUENCE;

CREATE TABLE user (
	id                      BIGINT(20) NOT NULL auto_increment,
	account_non_expired     BIT(1) NOT NULL,
	account_non_locked      BIT(1) NOT NULL,
	credentials_non_expired BIT(1) NOT NULL,
	enabled                 BIT(1) NOT NULL,
	password                VARCHAR(50) NULL DEFAULT NULL,
	username                VARCHAR(50) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE (username)
);
CREATE INDEX ux_username ON user(username);

CREATE TABLE role (
	id          BIGINT(20) NOT NULL auto_increment,
	authority   VARCHAR(50) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE (authority)
);
CREATE INDEX ux_authority ON role(authority);

CREATE TABLE users_roles (
	user_id BIGINT(20) NOT NULL,
	role_id BIGINT(20) NOT NULL
);
CREATE INDEX ix_user_id ON users_roles(user_id);
CREATE INDEX ix_role_id ON users_roles(role_id);

CREATE SEQUENCE HIBERNATE_SEQUENCE
   MINVALUE 1
   MAXVALUE 9999999999999999
   START WITH 1
   INCREMENT BY 100
   CACHE 100;
