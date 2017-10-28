-- Animedia tables by App Builders
-- Created by Erick Perez, CEO App Builders
-- Version V.1.0

-- This file gonna contains tables in MySQL and coments
-- This tables are a mirror of SQL Tables diagram, the conection can be undertanded easiest in the diagram
-- Please understand before pull/push 

-- This table handle the users
CREATE TABLE IF NOT EXISTS user (
	id BIGINT NOT NULL AUTO_INCREMENT,
	login VARCHAR(100) NOT NULL,
	slug VARCHAR(255) NOT NULL,
	fbid VARCHAR(255) NOT NULL,
	password VARCHAR(100) NOT NULL,
	nicename VARCHAR(100) NOT NULL,
	email VARCHAR(150) NOT NULL,
	status VARCHAR(60) NOT NULL DEFAULT '',
	type VARCHAR(150) NOT NULL DEFAULT '',
	created DATETIME NOT NULL,
	modified DATETIME NOT NULL,
	PRIMARY KEY pk_id (id),
	KEY idx_login (login),
	KEY idx_slug (slug),
	KEY idx_fbid (fbid)
) DEFAULT CHARACTER SET = UTF8;

-- This table handle meta information for users
CREATE TABLE IF NOT EXISTS user_meta (
	id BIGINT NOT NULL AUTO_INCREMENT,
	user_id BIGINT NOT NULL,
	name VARCHAR(200) NOT NULL,
	value TEXT NOT NULL,
	PRIMARY KEY pk_id (id),
	UNIQUE uk_user_name (user_id, name),
	KEY idx_user_name (user_id, name),
	KEY idx_name (name)
) DEFAULT CHARACTER SET = UTF8;

-- This table handle attachments
CREATE TABLE IF NOT EXISTS attachment (
	id BIGINT NOT NULL AUTO_INCREMENT,
	slug VARCHAR(255) NOT NULL,
	name VARCHAR(255) NOT NULL,
	attachment VARCHAR(255) NOT NULL,
	mime VARCHAR(255) NOT NULL,
	source VARCHAR(55) NOT NULL,
	created DATETIME NOT NULL,
	modified DATETIME NOT NULL,
	PRIMARY KEY pk_id (id)
) DEFAULT CHARACTER SET = UTF8;

-- This table handle records as a binnacle with the purpose to log everything
CREATE TABLE IF NOT EXISTS binnacle (
	id BIGINT NOT NULL AUTO_INCREMENT,
	user_id BIGINT NOT NULL,
	event VARCHAR(100) NOT NULL,
	instance VARCHAR(100) NOT NULL,
	param_a VARCHAR(100) NOT NULL,
	param_b VARCHAR(100) NOT NULL,
	param_c VARCHAR(100) NOT NULL,
	param_d MEDIUMTEXT NOT NULL,
	origin VARCHAR(255) NOT NULL,
	value int(11) NOT NULL,
	created DATETIME NOT NULL,
	modified DATETIME NOT NULL,
	PRIMARY KEY pk_id (id),
	UNIQUE KEY uk_user_instance (user_id, instance),
	KEY key_user (user_id)
) DEFAULT CHARACTER SET = UTF8;

CREATE TABLE IF NOT EXISTS anime (
	id BIGINT NOT NULL AUTO_INCREMENT,
	name VARCHAR(255) NOT NULL,
	description MEDIUMTEXT NOT NULL,
	year INT(20) NOT NULL,
	created DATETIME NOT NULL,
	modified DATETIME NOT NULL,
	PRIMARY KEY pk_id (id),
	UNIQUE uk_anime_name (name),
	KEY idx_name (name)
) DEFAULT CHARACTER SET = UTF8;

-- This table handle meta information for users
CREATE TABLE IF NOT EXISTS anime_meta (
	id BIGINT NOT NULL AUTO_INCREMENT,
	anime_id BIGINT NOT NULL,
	name VARCHAR(200) NOT NULL,
	value TEXT NOT NULL,
	PRIMARY KEY pk_id (id),
	UNIQUE uk_anime_name (anime_id, name),
	KEY idx_anime_name (anime_id, name),
	KEY idx_name (name)
) DEFAULT CHARACTER SET = UTF8;

-- Type: 1 - chapter, 2 - ova, 3 - movie, 4 - special
-- Audio: 1 - jp/spa, 2 - latino
CREATE TABLE IF NOT EXISTS media (
	id BIGINT NOT NULL AUTO_INCREMENT,
	anime_id BIGINT NOT NULL,
	number INT(100) NOT NULL,
	name VARCHAR(255) NOT NULL,
	description VARCHAR(255) NOT NULL,
	type VARCHAR(50) NOT NULL,
	audio VARCHAR(50) NOT NULL,
	created DATETIME NOT NULL,
	modified DATETIME NOT NULL,
	PRIMARY KEY pk_id (id),
	UNIQUE uk_media_number (anime_id, number, type, audio),
	KEY idx_anime (anime_id),
	KEY idx_type (type),
	KEY idx_audio (audio)
) DEFAULT CHARACTER SET = UTF8;

-- This table handle meta information for users
CREATE TABLE IF NOT EXISTS media_meta (
	id BIGINT NOT NULL AUTO_INCREMENT,
	media_id BIGINT NOT NULL,
	name VARCHAR(200) NOT NULL,
	value TEXT NOT NULL,
	PRIMARY KEY pk_id (id),
	UNIQUE uk_media_name (media_id, name),
	KEY idx_media_name (media_id, name),
	KEY idx_name (name)
) DEFAULT CHARACTER SET = UTF8;

CREATE TABLE IF NOT EXISTS genre (
	id BIGINT NOT NULL AUTO_INCREMENT,
	name VARCHAR(100) NOT NULL,
	created DATETIME NOT NULL,
	modified DATETIME NOT NULL,
	PRIMARY KEY pk_id (id)
) DEFAULT CHARACTER SET = UTF8;

CREATE TABLE IF NOT EXISTS serie_genre (
	serie_id BIGINT NOT NULL,
	genre_id BIGINT NOT NULL
) DEFAULT CHARACTER SET = UTF8;

CREATE TABLE IF NOT EXISTS serie_media (
	serie_id BIGINT NOT NULL,
	media_id BIGINT NOT NULL
) DEFAULT CHARACTER SET = UTF8;






