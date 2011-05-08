DROP TABLE IF EXISTS dict;
CREATE TABLE dict (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  name VARCHAR(128),
  orig_doc LONGBLOB,
  utf8_text LONGTEXT,
  status VARCHAR(32),
  encoding VARCHAR(32),
  preview BLOB,
  PRIMARY KEY(id),
  INDEX dicts_FKIndex2(user_id)
);

DROP TABLE IF EXISTS dict_word;
CREATE TABLE dict_word (
  id BIGINT NOT NULL AUTO_INCREMENT,
  dict_id BIGINT NOT NULL,
  word_id BIGINT NOT NULL,
  PRIMARY KEY(id, dict_id, word_id),
  INDEX dict_word_FKIndex1(dict_id),
  INDEX dict_word_FKIndex2(word_id)
);

DROP TABLE IF EXISTS lang;
CREATE TABLE lang (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(16) NULL,
  code VARCHAR(2) NULL,
  PRIMARY KEY(id),
  UNIQUE INDEX langs_Unique(code)
);

DROP TABLE IF EXISTS service;
CREATE TABLE service (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(32) NULL,
  req_pattern VARCHAR(255) NULL,
  res_pattern VARCHAR(255) NULL,
  PRIMARY KEY(id)
);

DROP TABLE IF EXISTS translation;
CREATE TABLE translation (
  id BIGINT NOT NULL AUTO_INCREMENT,
  translators_service_id BIGINT NOT NULL,
  word_id BIGINT NOT NULL,
  translator_id BIGINT NOT NULL,
  value VARCHAR(64) NULL,
  PRIMARY KEY(id),
  INDEX translations_FKIndex1(translator_id, translators_service_id),
  INDEX translations_FKIndex2(word_id)
);

DROP TABLE IF EXISTS translator;
CREATE TABLE translator (
  id BIGINT NOT NULL AUTO_INCREMENT,
  service_id BIGINT NOT NULL,
  dest_lang_id BIGINT NOT NULL,
  src_lang_id BIGINT NOT NULL,
  PRIMARY KEY(id, service_id),
  INDEX lang_translator_FKIndex1(src_lang_id),
  INDEX lang_translator_FKIndex2(dest_lang_id),
  INDEX lang_translators_FKIndex3(service_id)
);

DROP TABLE IF EXISTS user;
CREATE TABLE user (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(32) NULL,
  password VARCHAR(32) NULL,
  enabled BOOL NOT NULL DEFAULT TRUE,
  PRIMARY KEY(id)
);

INSERT INTO user VALUES (NULL, 'user', 'pass', 1);
INSERT INTO user VALUES (NULL, 'helicobacter', 'pilory', 1);

DROP TABLE IF EXISTS word;
CREATE TABLE word (
  id BIGINT NOT NULL AUTO_INCREMENT,
  lang_id BIGINT NOT NULL,
  value VARCHAR(32) NOT NULL,
  PRIMARY KEY(id),
  INDEX words_FKIndex1(lang_id),
  UNIQUE INDEX words_Unique(value)
);


