INSERT INTO transition VALUES (DEFAULT, 'LOAD', 'PERSISTED', 'LOADING', 'uploadHandler');
INSERT INTO transition VALUES (DEFAULT, 'OK', 'LOADING', 'STORING', 'storeHandler');
INSERT INTO transition VALUES (DEFAULT, 'FAIL', 'LOADING', 'PERSISTED', 'dummyHandler');
INSERT INTO transition VALUES (DEFAULT, 'STORE', 'LOADED', 'STORING', 'storeHandler');
INSERT INTO transition VALUES (DEFAULT, 'STORE', 'STORED', 'STORING', 'storeHandler');
INSERT INTO transition VALUES (DEFAULT, 'FAIL', 'STORING', 'LOADED', 'dummyHandler');
INSERT INTO transition VALUES (DEFAULT, 'WAIT', 'STORING', 'LOADED', 'storeHandler');
INSERT INTO transition VALUES (DEFAULT, 'OK', 'STORING', 'PARSING', 'parseHandler');
INSERT INTO transition VALUES (DEFAULT, 'PARSE', 'STORED', 'PARSING', 'parseHandler');
INSERT INTO transition VALUES (DEFAULT, 'PARSE', 'PARSED', 'PARSING', 'parseHandler');
INSERT INTO transition VALUES (DEFAULT, 'OK', 'PARSING', 'PARSED', 'dummyHandler');
INSERT INTO transition VALUES (DEFAULT, 'FAIL', 'PARSING', 'STORED', 'dummyHandler');
INSERT INTO transition VALUES (DEFAULT, 'TRANSLATE', 'PARSED', 'TRANSLATING', 'translateHandler');
INSERT INTO transition VALUES (DEFAULT, 'TRANSLATE', 'TRANSLATED', 'TRANSLATING', 'translateHandler');
INSERT INTO transition VALUES (DEFAULT, 'OK', 'TRANSLATING', 'TRANSLATED', 'dummyHandler');
INSERT INTO transition VALUES (DEFAULT, 'FAIL', 'TRANSLATING', 'PARSED', 'dummyHandler');


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
INSERT INTO lang VALUES ('la', 'Latin', 'ISO-8859-1');
INSERT INTO lang VALUES ('da', 'Danish', 'ISO-8859-1,UTF-8');
INSERT INTO lang VALUES ('pl', 'Polish', 'UTF-8,Windows-1250,ISO-8859-2');

INSERT INTO user VALUES (DEFAULT, 'guest', '', 'GUEST', 1, 'en');
INSERT INTO user VALUES (DEFAULT, 'user', 'pass', 'USER', 1, 'en');
INSERT INTO user VALUES (DEFAULT, 'helicobacter', 'pilory', 'USER', 1, 'en');
INSERT INTO user VALUES (DEFAULT, 'admin', 'admin', 'ADMIN', 1, 'en');

INSERT INTO translator_provider VALUES (0, 'Bing', 'api.microsofttranslator.com', '<{1}>{0}</{2}>',
'http://api.microsofttranslator.com/V2/Http.svc/Translate?appId=CDCB8BFFDD9E4C3054316BC629E82D1E39CA585C&text={0}&from={1}&to={2}');
INSERT INTO translator_provider VALUES (1,'Google','',
'{1}<span id=result_box class=\"short_text\"><span {2}>{0}</span>{3}','http://translate.google.com/?sl={1}&tl={2}&text={0}');

INSERT INTO translator VALUES (0, 0, 'en', 'ru');
INSERT INTO translator VALUES (1, 0, 'fr', 'ru');
INSERT INTO translator VALUES (2, 0, 'de', 'ru');
INSERT INTO translator VALUES (3, 1, 'en', 'ru');
INSERT INTO translator VALUES (4, 1, 'fr', 'ru');
INSERT INTO translator VALUES (5, 1, 'de', 'ru');
INSERT INTO translator VALUES (6, 1, 'el', 'ru');
INSERT INTO translator VALUES (7, 1, 'sv', 'ru');
INSERT INTO translator VALUES (8, 1, 'is', 'ru');
INSERT INTO translator VALUES (9, 1, 'da', 'ru');
INSERT INTO translator VALUES (10, 1, 'pl', 'ru');

--
-- CREATE  TABLE IF NOT EXISTS `service` (
--   `id` BIGINT NOT NULL AUTO_INCREMENT ,
--   `title` VARCHAR(32) NULL DEFAULT NULL ,
--   `host` VARCHAR(64) NULL DEFAULT NULL ,
--   `req_pattern` VARCHAR(255) NULL DEFAULT NULL ,
--   `res_pattern` VARCHAR(255) NULL DEFAULT NULL ,
--
-- CREATE  TABLE IF NOT EXISTS `translator` (
--   `id` BIGINT NOT NULL AUTO_INCREMENT ,
--   `service_id` BIGINT NOT NULL ,
--   `src_lang_id` VARCHAR(2) NOT NULL ,
--   `dest_lang_id` VARCHAR(2) NOT NULL ,
--
