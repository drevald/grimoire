
CREATE SEQUENCE hibernate_sequence START 1;

-- -----------------------------------------------------
-- Table `transition`
-- -----------------------------------------------------
DROP TABLE IF EXISTS job;
DROP TABLE IF EXISTS transition;

CREATE TABLE IF NOT EXISTS transition (
  id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY
	(START WITH 1 INCREMENT BY 17),
  event varchar(32),
  src_status varchar(32),
  dest_status varchar(32),
  handler_name varchar(32),
  PRIMARY KEY(id));

-- -----------------------------------------------------
-- Table `lang`
-- -----------------------------------------------------
DROP TABLE IF EXISTS dict_word;
DROP TABLE IF EXISTS dict;
DROP TABLE IF EXISTS translation;
DROP TABLE IF EXISTS translator;
DROP TABLE IF EXISTS account_lang;
DROP TABLE IF EXISTS lang;

CREATE TABLE IF NOT EXISTS lang (
  id varchar(2),
  "name" varchar(16),
  encodings varchar(255),
  PRIMARY KEY(id));

-- -----------------------------------------------------
-- Table `account`
-- -----------------------------------------------------
DROP TABLE IF EXISTS "account";

CREATE  TABLE IF NOT EXISTS "account" (
  id bigserial NOT NULL,
  accountname varchar(32) NOT NULL UNIQUE,
  password varchar(32) NOT NULL ,
  role varchar(8) NOT NULL DEFAULT 'USER',
  enabled boolean NOT NULL DEFAULT TRUE ,
  native_lang_id varchar(2),
  PRIMARY KEY (id));

-- -----------------------------------------------------
-- Table `text`
-- -----------------------------------------------------
DROP TABLE IF EXISTS text;

CREATE  TABLE IF NOT EXISTS text (
  id bigserial NOT NULL,
  orig_path varchar(128) NOT NULL ,
  utf8_path varchar(128) NOT NULL ,
  encoding varchar(32) NOT NULL,
  orig_doc bytea,
  utf8_text  bytea,
  PRIMARY KEY(id));

-- -----------------------------------------------------
-- Table `dict`
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS dict (
  id BIGSERIAL NOT NULL,
  "name" varchar(128),
  orig_doc bytea,
  utf8_text  text,
  status varchar(32),
  "encoding" varchar(32),
  preview bytea,
  lang_id varchar(2) NOT NULL ,
  account_id BIGINT NOT NULL ,
  text_id BIGINT NULL ,
  PRIMARY KEY(id) ,
  CONSTRAINT fk_dict_lang1 FOREIGN KEY (lang_id) REFERENCES lang(id)
	ON DELETE CASCADE 
	ON UPDATE CASCADE,
  CONSTRAINT fk_dict_account1 FOREIGN KEY (account_id) REFERENCES "account"(id)
	ON DELETE CASCADE 
	ON UPDATE CASCADE,
  CONSTRAINT fk_dict_text1 FOREIGN KEY (text_id) REFERENCES text(id)
	ON DELETE NO ACTION 
	ON UPDATE NO ACTION);

-- -----------------------------------------------------
-- Table `job`
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS job (
  id bigserial NOT NULL,
  progress int NOT NULL DEFAULT 0 ,
  details varchar(255),
  active   boolean  NOT NULL DEFAULT FALSE ,
  transition_id bigint NOT NULL ,
  dict_id bigint NOT NULL,
  PRIMARY KEY(id),
  CONSTRAINT fk_job_transition1 FOREIGN KEY (transition_id) REFERENCES transition(id)
	ON DELETE CASCADE 
	ON UPDATE CASCADE,
  CONSTRAINT fk_job_dict1 FOREIGN KEY (dict_id) REFERENCES dict(id)
	ON DELETE CASCADE 
	ON UPDATE CASCADE);

-- -----------------------------------------------------
-- Table `translator_provider`
-- -----------------------------------------------------
DROP TABLE IF EXISTS translator_provider;

CREATE  TABLE IF NOT EXISTS translator_provider (
  id bigint NOT NULL ,
  title varchar(32) ,
  host varchar(64) ,
  req_pattern varchar(255) ,
  res_pattern varchar(255) ,
  PRIMARY KEY (id));

-- -----------------------------------------------------
-- Table `word`
-- -----------------------------------------------------
DROP TABLE IF EXISTS word;

CREATE TABLE IF NOT EXISTS word (
  id bigserial NOT NULL,
  lang_id varchar(2) NOT NULL ,
  value varchar(32) NOT NULL ,
  UNIQUE(value, lang_id),
  PRIMARY KEY (id));

-- -----------------------------------------------------
-- Table `translator`
-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS translator (
  id bigint NOT NULL ,
  service_id bigint NOT NULL ,
  src_lang_id varchar(2) NOT NULL ,
  dest_lang_id varchar(2) NOT NULL,
  PRIMARY KEY(id),
  CONSTRAINT fk_translator_service1 FOREIGN KEY (service_id) REFERENCES translator_provider(id)
	ON DELETE CASCADE 
	ON UPDATE CASCADE,
   CONSTRAINT fk_translator_src_lang1 FOREIGN KEY (src_lang_id) REFERENCES lang(id)
	ON DELETE CASCADE 
	ON UPDATE CASCADE,
  CONSTRAINT fk_translator_dest_lang2 FOREIGN KEY (dest_lang_id) REFERENCES lang(id)
	ON DELETE CASCADE 
	ON UPDATE CASCADE);

-- -----------------------------------------------------
-- Table `translation`
-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS translation (
  id bigserial NOT NULL,
  value varchar(64),
  word_id bigint NOT NULL UNIQUE,
  translator_id bigint,
  account_id bigint,
  pre_text varchar(128),
  post_text  varchar(128),
  PRIMARY KEY(id),
  CONSTRAINT fk_translation_word1 FOREIGN KEY (word_id) REFERENCES word(id)
	ON DELETE CASCADE 
	ON UPDATE CASCADE,
  CONSTRAINT fk_translation_translator1 FOREIGN KEY (translator_id) REFERENCES translator(id)
	ON DELETE CASCADE 
	ON UPDATE CASCADE);

-- -----------------------------------------------------
-- Table `dict_word`
-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS dict_word (
  id bigserial NOT NULL,
  counter bigint NOT NULL,
  dict_id bigint NOT NULL,
  word_id bigint NOT NULL,
  translation varchar(64), 
  translator varchar(32),
  occurances text,
  PRIMARY KEY(id),
  CONSTRAINT fk_dict_word_dict1 FOREIGN KEY(dict_id) REFERENCES dict(id)
	ON DELETE CASCADE 
	ON UPDATE CASCADE,
  CONSTRAINT fk_dict_word_word1 FOREIGN KEY(word_id) REFERENCES word(id)
	ON DELETE CASCADE 
	ON UPDATE CASCADE);

-- -----------------------------------------------------
-- Table `account_lang`
-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS account_lang (
  id bigserial NOT NULL,
  account_id bigserial NOT NULL,
  lang_id varchar(2) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_account_lang_account1 FOREIGN KEY(account_id) REFERENCES "account"(id)
	ON DELETE CASCADE 
	ON UPDATE CASCADE,
  CONSTRAINT fk_account_lang_lang1 FOREIGN KEY(lang_id) REFERENCES lang(id)
	ON DELETE CASCADE 
	ON UPDATE CASCADE);

