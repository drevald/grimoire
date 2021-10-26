INSERT INTO transition(event, src_status, dest_status, handler_name)
	VALUES ('LOAD', 'PERSISTED', 'LOADING', 'uploadHandler');
INSERT INTO transition(event, src_status, dest_status, handler_name) 
	VALUES ('OK', 'LOADING', 'STORING', 'storeHandler');
INSERT INTO transition(event, src_status, dest_status, handler_name) 
	VALUES ('FAIL', 'LOADING', 'PERSISTED', 'dummyHandler');
INSERT INTO transition(event, src_status, dest_status, handler_name) 
	VALUES ('STORE', 'LOADED', 'STORING', 'storeHandler');
INSERT INTO transition(event, src_status, dest_status, handler_name) 
	VALUES ('STORE', 'STORED', 'STORING', 'storeHandler');
INSERT INTO transition(event, src_status, dest_status, handler_name)
	VALUES ('FAIL', 'STORING', 'LOADED', 'dummyHandler');
INSERT INTO transition(event, src_status, dest_status, handler_name) 
	VALUES ('WAIT', 'STORING', 'LOADED', 'storeHandler');
INSERT INTO transition(event, src_status, dest_status, handler_name) 
	VALUES ('OK', 'STORING', 'PARSING', 'parseHandler');
INSERT INTO transition(event, src_status, dest_status, handler_name) 
	VALUES ('PARSE', 'STORED', 'PARSING', 'parseHandler');
INSERT INTO transition(event, src_status, dest_status, handler_name) 
	VALUES ('PARSE', 'PARSED', 'PARSING', 'parseHandler');
INSERT INTO transition(event, src_status, dest_status, handler_name) 
	VALUES ('OK', 'PARSING', 'PARSED', 'dummyHandler');
INSERT INTO transition(event, src_status, dest_status, handler_name) 
	VALUES ('FAIL', 'PARSING', 'STORED', 'dummyHandler');
INSERT INTO transition(event, src_status, dest_status, handler_name) 
	VALUES ('TRANSLATE', 'PARSED', 'TRANSLATING', 'translateHandler');
INSERT INTO transition(event, src_status, dest_status, handler_name) 
	VALUES ('TRANSLATE', 'TRANSLATED', 'TRANSLATING', 'translateHandler');
INSERT INTO transition(event, src_status, dest_status, handler_name) 
	VALUES ('OK', 'TRANSLATING', 'TRANSLATED', 'dummyHandler');
INSERT INTO transition(event, src_status, dest_status, handler_name) 
	VALUES ('FAIL', 'TRANSLATING', 'PARSED', 'dummyHandler');


INSERT INTO lang(id,"name",encodings) VALUES ('ru', 'Russian', 'Windows-1251,KOI8-R,Cp866,UTF-8');
INSERT INTO lang(id,"name",encodings) VALUES ('zh', 'Chinese', 'UTF-8');
INSERT INTO lang(id,"name",encodings) VALUES ('en', 'English', 'ISO-8859-1,UTF-8');
INSERT INTO lang(id,"name",encodings) VALUES ('fi', 'Finnish', 'ISO-8859-1,UTF-8');
INSERT INTO lang(id,"name",encodings) VALUES ('fr', 'French', 'ISO-8859-1,UTF-8');
INSERT INTO lang(id,"name",encodings) VALUES ('de', 'German', 'ISO-8859-1,UTF-8');
INSERT INTO lang(id,"name",encodings) VALUES ('el', 'Greek', 'UTF-8');
INSERT INTO lang(id,"name",encodings) VALUES ('is', 'Icelandic', 'ISO-8859-1,UTF-8');
INSERT INTO lang(id,"name",encodings) VALUES ('ja', 'Japanese', 'UTF-8');
INSERT INTO lang(id,"name",encodings) VALUES ('es', 'Spanish', 'ISO-8859-1,UTF-8');
INSERT INTO lang(id,"name",encodings) VALUES ('sv', 'Swedish', 'ISO-8859-1,UTF-8');
INSERT INTO lang(id,"name",encodings) VALUES ('ar', 'Arabic', 'UTF-8');
INSERT INTO lang(id,"name",encodings) VALUES ('la', 'Latin', 'ISO-8859-1');
INSERT INTO lang(id,"name",encodings) VALUES ('da', 'Danish', 'ISO-8859-1,UTF-8');
INSERT INTO lang(id,"name",encodings) VALUES ('pl', 'Polish', 'UTF-8,Windows-1250,ISO-8859-2');

INSERT INTO "user"(username, password, role, enabled, native_lang_id) 
	VALUES ('guest', '', 'GUEST', True, 'en');
INSERT INTO "user"(username, password, role, enabled, native_lang_id) 
	VALUES ('user', 'pass', 'USER', True, 'en');
INSERT INTO "user"(username, password, role, enabled,native_lang_id) 
	VALUES ('helicobacter', 'pilory', 'USER', True, 'en');
INSERT INTO "user"(username, password, role, enabled, native_lang_id) 
	VALUES ('admin', 'admin', 'ADMIN', True, 'en');

INSERT INTO translator_provider(id, title, host, req_pattern, res_pattern) 
	VALUES (0, 'Bing', 'api.microsofttranslator.com', '<{1}>{0}</{2}>',
'http://api.microsofttranslator.com/V2/Http.svc/Translate?appId=CDCB8BFFDD9E4C3054316BC629E82D1E39CA585C&text={0}&from={1}&to={2}');
INSERT INTO translator_provider(id, title, host, req_pattern, res_pattern) 
	VALUES (1,'Google','',
'{1}<span id=result_box class=\"short_text\"><span {2}>{0}</span>{3}','http://translate.google.com/?sl={1}&tl={2}&text={0}');

INSERT INTO translator(id,service_id,src_lang_id,dest_lang_id) VALUES (1, 0, 'fr', 'ru');
INSERT INTO translator(id,service_id,src_lang_id,dest_lang_id) VALUES (2, 0, 'de', 'ru');
INSERT INTO translator(id,service_id,src_lang_id,dest_lang_id) VALUES (3, 1, 'en', 'ru');
INSERT INTO translator(id,service_id,src_lang_id,dest_lang_id) VALUES (4, 1, 'fr', 'ru');
INSERT INTO translator(id,service_id,src_lang_id,dest_lang_id) VALUES (5, 1, 'de', 'ru');
INSERT INTO translator(id,service_id,src_lang_id,dest_lang_id) VALUES (6, 1, 'el', 'ru');
INSERT INTO translator(id,service_id,src_lang_id,dest_lang_id) VALUES (7, 1, 'sv', 'ru');
INSERT INTO translator(id,service_id,src_lang_id,dest_lang_id) VALUES (8, 1, 'is', 'ru');
INSERT INTO translator(id,service_id,src_lang_id,dest_lang_id) VALUES (9, 1, 'da', 'ru');
INSERT INTO translator(id,service_id,src_lang_id,dest_lang_id) VALUES (10, 1, 'pl', 'ru');

--
-- CREATE  TABLE IF NOT EXISTS service (
--   id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
--   title varchar(32),
--   host varchar(64),
--   req_pattern varchar(255),
--   res_pattern varchar(255));
--
-- CREATE  TABLE IF NOT EXISTS translator (
--   id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
--   service_id bigint NOT NULL,
--   src_lang_id varchar(2) NOT NULL ,
--   dest_lang_id varchar(2) NOT NULL);
--
