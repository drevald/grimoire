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
INSERT INTO transition VALUES (NULL, 'WAIT', 'STORING', 'LOADED', 'storeHandler');
INSERT INTO transition VALUES (NULL, 'STORE', 'LOADED', 'STORING', 'storeHandler');
INSERT INTO transition VALUES (NULL, 'STORE', 'STORED', 'STORING', 'storeHandler');
INSERT INTO transition VALUES (NULL, 'OK', 'STORING', 'STORED', 'dummyHandler');
INSERT INTO transition VALUES (NULL, 'PARSE', 'STORED', 'PARSING', 'parseHandler');
INSERT INTO transition VALUES (NULL, 'PARSE', 'PARSED', 'PARSING', 'parseHandler');
INSERT INTO transition VALUES (NULL, 'OK', 'PARSING', 'PARSED', 'dummyHandler');
INSERT INTO transition VALUES (NULL, 'TRANSLATE', 'PARSED', 'TRANSLATING', 'translateHandler');
INSERT INTO transition VALUES (NULL, 'TRANSLATE', 'TRANSLATED', 'TRANSLATING', 'translateHandler');
INSERT INTO transition VALUES (NULL, 'OK', 'TRANSLATING', 'TRANSLATED', 'dummyHandler');

DROP TABLE IF EXISTS job;
CREATE TABLE job (
       id BIGINT NOT NULL AUTO_INCREMENT,
       trans_id BIGINT NOT NULL,
       dict_id BIGINT NOT NULL,
       progress INT NOT NULL DEFAULT 0,
       active BOOL NOT NULL DEFAULT FALSE,
       PRIMARY KEY(id)
);

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
  name VARCHAR(16) NULL,
  code VARCHAR(2) NULL,
  encodings VARCHAR(255),
  PRIMARY KEY(id),
  UNIQUE INDEX langs_Unique(code)
);

INSERT INTO lang VALUES (NULL, 'Albanian', 'sq', 'UTF-8');
INSERT INTO lang VALUES (NULL, 'Arabic', 'ar', 'UTF-8');
INSERT INTO lang VALUES (NULL, 'Belarusian', 'be', 'UTF-8');
INSERT INTO lang VALUES (NULL, 'Bulgarian', 'bg', 'UTF-8');
INSERT INTO lang VALUES (NULL, 'Catalan', 'ca', 'UTF-8');
INSERT INTO lang VALUES (NULL, 'Chinese', 'zh', 'UTF-8');
INSERT INTO lang VALUES (NULL, 'Croatian', 'hr', 'UTF-8');
INSERT INTO lang VALUES (NULL, 'Czech', 'cs', 'UTF-8');
INSERT INTO lang VALUES (NULL, 'Danish', 'da', 'UTF-8');
INSERT INTO lang VALUES (NULL, 'Dutch', 'nl', 'UTF-8');
INSERT INTO lang VALUES (NULL, 'English', 'en', 'ISO-8859-1,UTF-8');
INSERT INTO lang VALUES (NULL, 'Estonian', 'et', 'ISO-8859-1,UTF-8');
INSERT INTO lang VALUES (NULL, 'Finnish', 'fi', 'ISO-8859-1,UTF-8');
INSERT INTO lang VALUES (NULL, 'French', 'fr', 'ISO-8859-1,UTF-8');
INSERT INTO lang VALUES (NULL, 'German', 'de', 'ISO-8859-1,UTF-8');
INSERT INTO lang VALUES (NULL, 'Greek', 'el', 'UTF-8');
INSERT INTO lang VALUES (NULL, 'Hebrew', 'iw', 'UTF-8');
INSERT INTO lang VALUES (NULL, 'Hindi', 'hi', 'UTF-8');
INSERT INTO lang VALUES (NULL, 'Hungarian', 'hu', 'UTF-8');
INSERT INTO lang VALUES (NULL, 'Icelandic', 'is', 'ISO-8859-1,UTF-8');
INSERT INTO lang VALUES (NULL, 'Italian', 'it', 'ISO-8859-1,UTF-8');
INSERT INTO lang VALUES (NULL, 'Japanese', 'ja', 'UTF-8');
INSERT INTO lang VALUES (NULL, 'Korean', 'ko', 'UTF-8');
INSERT INTO lang VALUES (NULL, 'Latvian', 'lv', 'UTF-8');
INSERT INTO lang VALUES (NULL, 'Lithuanian', 'lt', 'UTF-8');
INSERT INTO lang VALUES (NULL, 'Macedonian', 'mk', 'UTF-8');
INSERT INTO lang VALUES (NULL, 'Norwegian', 'no', 'ISO-8859-1,UTF-8');
INSERT INTO lang VALUES (NULL, 'Polish', 'pl', 'ISO-8859-1,UTF-8');
INSERT INTO lang VALUES (NULL, 'Portuguese', 'pt', 'ISO-8859-1,UTF-8');
INSERT INTO lang VALUES (NULL, 'Romanian', 'ro', 'ISO-8859-1,UTF-8');
INSERT INTO lang VALUES (NULL, 'Russian', 'ru', 'WIndows-1241,KOI8-R,Cp866,UTF-8');
INSERT INTO lang VALUES (NULL, 'Serbian', 'sr', 'UTF-8');
INSERT INTO lang VALUES (NULL, 'Slovak', 'sk', 'UTF-8');
INSERT INTO lang VALUES (NULL, 'Slovenian', 'sl', 'UTF-8');
INSERT INTO lang VALUES (NULL, 'Spanish', 'es', 'ISO-8859-1,UTF-8');
INSERT INTO lang VALUES (NULL, 'Swedish', 'sv', 'ISO-8859-1,UTF-8');
INSERT INTO lang VALUES (NULL, 'Thai', 'th', 'UTF-8');
INSERT INTO lang VALUES (NULL, 'Turkish', 'tr', 'UTF-8');
INSERT INTO lang VALUES (NULL, 'Ukrainian', 'uk', 'UTF-8');
INSERT INTO lang VALUES (NULL, 'Vietnamese', 'vi', 'UTF-8');

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


