-- todo add cascade deletion for dependent entities

DROP TABLE IF EXISTS transition;
CREATE TABLE transition (
       id BIGINT NOT NULL AUTO_INCREMENT,
       event VARCHAR(32),
       src_status VARCHAR(32),
       dest_status VARCHAR(32),
       handler_name VARCHAR(32),
       PRIMARY KEY(id)
);

INSERT INTO transition VALUES (NULL, 'LOAD', 'PERSISTED', 'LOADING', 'uploadHandler');
INSERT INTO transition VALUES (NULL, 'OK', 'LOADING', 'STORING', 'storeHandler');
INSERT INTO transition VALUES (NULL, 'FAIL', 'LOADING', 'PERSISTED', 'dummyHandler');
INSERT INTO transition VALUES (NULL, 'STORE', 'LOADED', 'STORING', 'storeHandler');
INSERT INTO transition VALUES (NULL, 'STORE', 'STORED', 'STORING', 'storeHandler');
INSERT INTO transition VALUES (NULL, 'FAIL', 'STORING', 'LOADED', 'dummyHandler');
INSERT INTO transition VALUES (NULL, 'WAIT', 'STORING', 'LOADED', 'storeHandler');
INSERT INTO transition VALUES (NULL, 'OK', 'STORING', 'PARSING', 'parseHandler');
INSERT INTO transition VALUES (NULL, 'PARSE', 'STORED', 'PARSING', 'parseHandler');
INSERT INTO transition VALUES (NULL, 'PARSE', 'PARSED', 'PARSING', 'parseHandler');
INSERT INTO transition VALUES (NULL, 'OK', 'PARSING', 'PARSED', 'dummyHandler');
INSERT INTO transition VALUES (NULL, 'FAIL', 'PARSING', 'STORED', 'dummyHandler');
INSERT INTO transition VALUES (NULL, 'TRANSLATE', 'PARSED', 'TRANSLATING', 'translateHandler');
INSERT INTO transition VALUES (NULL, 'TRANSLATE', 'TRANSLATED', 'TRANSLATING', 'translateHandler');
INSERT INTO transition VALUES (NULL, 'OK', 'TRANSLATING', 'TRANSLATED', 'dummyHandler');
INSERT INTO transition VALUES (NULL, 'FAIL', 'TRANSLATING', 'PARSED', 'dummyHandler');

DROP TABLE IF EXISTS job;
CREATE TABLE job (
       id BIGINT NOT NULL AUTO_INCREMENT,
       trans_id BIGINT NOT NULL,
       dict_id BIGINT NOT NULL,
       progress INT NOT NULL DEFAULT 0,
       details VARCHAR(255),
       active BOOL NOT NULL DEFAULT FALSE,
       PRIMARY KEY(id)
);


-- CREATE TABLE BespokeWaitConditionParameter (
--   id BIGINT(20) NOT NULL AUTO_INCREMENT,
--   bespokeWaitConditionId BIGINT(20) NULL,
--   name VARCHAR(255) NULL,
--   description VARCHAR(255) NULL,
--   PRIMARY KEY(id),
--   UNIQUE INDEX BespokeWaitConditionParameter_AK(bespokeWaitConditionId, name),
--   FOREIGN KEY(bespokeWaitConditionId)
--     REFERENCES BespokeWaitCondition(id)
--       ON DELETE CASCADE
--       ON UPDATE CASCADE
-- )

DROP TABLE IF EXISTS lang;
CREATE TABLE lang (
  id VARCHAR(2) NULL,
  name VARCHAR(16) NULL,
  encodings VARCHAR(255),
  PRIMARY KEY(id)
);

INSERT INTO lang VALUES ('ru', 'Russian', 'Windows-1251,KOI8-R,Cp866,UTF-8');
INSERT INTO lang VALUES ('zh', 'Chinese', 'UTF-8');
INSERT INTO lang VALUES ('en', 'English', 'ISO-8859-1,UTF-8');
INSERT INTO lang VALUES ('fi', 'Finnish', 'ISO-8859-1,UTF-8');
INSERT INTO lang VALUES ('fr', 'French', 'ISO-8859-1,UTF-8');
INSERT INTO lang VALUES ('de', 'German', 'ISO-8859-1,UTF-8');
INSERT INTO lang VALUES ('el', 'Greek', 'UTF-8');
INSERT INTO lang VALUES ('is', 'Icelandic', 'ISO-8859-1,UTF-8');
INSERT INTO lang VALUES ('ja', 'Japanese', 'UTF-8');
INSERT INTO lang VALUES ('es', 'Spanish', 'ISO-8859-1,UTF-8');
INSERT INTO lang VALUES ('sv', 'Swedish', 'ISO-8859-1,UTF-8');
INSERT INTO lang VALUES ('ar', 'Arabic', 'UTF-8');

DROP TABLE IF EXISTS service;
CREATE TABLE service (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(32) NULL,
  host VARCHAR(64),
  req_pattern VARCHAR(255) NULL,
  res_pattern VARCHAR(255) NULL,
  PRIMARY KEY(id)
);

INSERT INTO service VALUES (NULL,'Google','translate.google.com','/translate_t?hl=is&ie=UTF-8&text="+str+"&sl=en&tl=is#',
       '<span id=result_box class=\"short_text\"><span[\\s\\w+=\"w+|.+#+\"]+>(\\w+)');

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
  dest_lang_id VARCHAR(2) NOT NULL,
  src_lang_id VARCHAR(2) NOT NULL,
  PRIMARY KEY(id, service_id),
  INDEX lang_translator_FKIndex1(src_lang_id),
  INDEX lang_translator_FKIndex2(dest_lang_id),
  INDEX lang_translators_FKIndex3(service_id)
);

INSERT INTO translator VALUES (1, 1, 1, 3);

DROP TABLE IF EXISTS user;
CREATE TABLE user (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(32) NOT NULL,
  password VARCHAR(32) NOT NULL,
  role VARCHAR(8) NOT NULL,
  enabled BOOL NOT NULL DEFAULT TRUE,
  PRIMARY KEY(id)
);

INSERT INTO user VALUES (NULL, 'guest', '', 'GUEST', 1);
INSERT INTO user VALUES (NULL, 'user', 'pass', 'USER', 1);
INSERT INTO user VALUES (NULL, 'helicobacter', 'pilory', 'USER', 1);
INSERT INTO user VALUES (NULL, 'admin', 'admin', 'ADMIN', 1);

DROP TABLE IF EXISTS dict_word;
CREATE TABLE dict_word (
  id BIGINT NOT NULL AUTO_INCREMENT,
  dict_id BIGINT NOT NULL,
  word_id BIGINT NOT NULL,
  counter BIGINT NOT NULL,
  PRIMARY KEY(id),
--  PRIMARY KEY(id, dict_id, word_id),
  FOREIGN KEY (dict_id) REFERENCES dict(id) ON DELETE CASCADE
--   FOREIGN KEY (word_id) REFERENCES word(id) ON DELETE CASCADE
--   INDEX dict_word_FKIndex1(dict_id),
--   INDEX dict_word_FKIndex2(word_id)
);

DROP TABLE IF EXISTS word;
CREATE TABLE word (
  id BIGINT NOT NULL AUTO_INCREMENT,
  lang_id VARCHAR(2) NOT NULL,
  value VARCHAR(32) NOT NULL,
  PRIMARY KEY(id),
  INDEX words_FKIndex1(lang_id),
  UNIQUE INDEX words_Unique(value, lang_id)
);

DROP TABLE IF EXISTS dict;
CREATE TABLE dict (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  lang_id VARCHAR(2),
  name VARCHAR(128),
  orig_doc LONGBLOB,
  utf8_text LONGTEXT,
  status VARCHAR(32),
  encoding VARCHAR(32),
  preview BLOB,
  PRIMARY KEY(id),
  INDEX dicts_FKIndex2(user_id)
);