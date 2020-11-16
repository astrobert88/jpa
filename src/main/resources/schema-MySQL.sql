-- Cleanup --
BEGIN;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS users_roles;
DROP TABLE IF EXISTS hibernate_sequence;
COMMIT;

-- Create tables --
BEGIN;
CREATE TABLE user (
	id                      BIGINT(20) NOT NULL,
	account_non_expired     BIT(1) NOT NULL,
	account_non_locked      BIT(1) NOT NULL,
	credentials_non_expired BIT(1) NOT NULL,
	enabled                 BIT(1) NOT NULL,
	password                VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8_general_ci',
	username                VARCHAR(50) NOT NULL COLLATE 'utf8_general_ci',
	PRIMARY KEY (id) USING BTREE,
	UNIQUE INDEX (username) USING BTREE
)
COLLATE='utf8_general_ci'
ENGINE=MyISAM
;

CREATE TABLE role (
	id          BIGINT(20) NOT NULL,
	authority   VARCHAR(50) NOT NULL COLLATE 'utf8_general_ci',
	PRIMARY KEY (id) USING BTREE,
	UNIQUE INDEX (authority) USING BTREE
)
COLLATE='utf8_general_ci'
ENGINE=MyISAM
;

CREATE TABLE users_roles (
	user_id BIGINT(20) NOT NULL,
	role_id BIGINT(20) NOT NULL,
	INDEX (role_id) USING BTREE,
	INDEX (user_id) USING BTREE
)
COLLATE='utf8_general_ci'
ENGINE=MyISAM
;

CREATE TABLE hibernate_sequence (
	next_val BIGINT(20) NULL DEFAULT NULL
)
COLLATE='utf8_general_ci'
ENGINE=MyISAM
;
COMMIT;
