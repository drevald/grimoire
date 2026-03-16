-- Add Scriptorium as a standard HTTP translation provider.
-- req_pattern: {0}=word, {1}=srcLang, {2}=destLang
-- res_pattern: MessageFormat parse pattern to extract translated word as {0}

-- req_pattern: {0}=word, {1}=srcLang, {2}=destLang
-- res_pattern: {5} anchors MessageFormat.parse() at pos 0 (captures preamble),
--              {0} captures translated word, {4} captures the rest
INSERT INTO translator_provider(title, host, method, req_pattern, res_pattern)
    VALUES ('Scriptorium', 'scriptorium.dobby', 'GET',
            'https://scriptorium.dobby/api/v1/translate/{1}/{0}/{2}',
            '{5}"translations":[{"word":"{0}",{4}]');

-- Ensure translator.id has a sequence (defined as plain bigint in initial schema).
CREATE SEQUENCE IF NOT EXISTS translator_id_seq;
ALTER TABLE translator ALTER COLUMN id SET DEFAULT nextval('translator_id_seq');
ALTER SEQUENCE translator_id_seq OWNED BY translator.id;
SELECT setval('translator_id_seq', COALESCE((SELECT MAX(id) FROM translator), 0) + 1, false);

-- Add translators for all language pairs (src -> ru).
INSERT INTO translator(service_id, src_lang_id, dest_lang_id)
    SELECT id, 'ar', 'ru' FROM translator_provider WHERE title = 'Scriptorium';
INSERT INTO translator(service_id, src_lang_id, dest_lang_id)
    SELECT id, 'da', 'ru' FROM translator_provider WHERE title = 'Scriptorium';
INSERT INTO translator(service_id, src_lang_id, dest_lang_id)
    SELECT id, 'de', 'ru' FROM translator_provider WHERE title = 'Scriptorium';
INSERT INTO translator(service_id, src_lang_id, dest_lang_id)
    SELECT id, 'el', 'ru' FROM translator_provider WHERE title = 'Scriptorium';
INSERT INTO translator(service_id, src_lang_id, dest_lang_id)
    SELECT id, 'en', 'ru' FROM translator_provider WHERE title = 'Scriptorium';
INSERT INTO translator(service_id, src_lang_id, dest_lang_id)
    SELECT id, 'es', 'ru' FROM translator_provider WHERE title = 'Scriptorium';
INSERT INTO translator(service_id, src_lang_id, dest_lang_id)
    SELECT id, 'fi', 'ru' FROM translator_provider WHERE title = 'Scriptorium';
INSERT INTO translator(service_id, src_lang_id, dest_lang_id)
    SELECT id, 'fr', 'ru' FROM translator_provider WHERE title = 'Scriptorium';
INSERT INTO translator(service_id, src_lang_id, dest_lang_id)
    SELECT id, 'is', 'ru' FROM translator_provider WHERE title = 'Scriptorium';
INSERT INTO translator(service_id, src_lang_id, dest_lang_id)
    SELECT id, 'ja', 'ru' FROM translator_provider WHERE title = 'Scriptorium';
INSERT INTO translator(service_id, src_lang_id, dest_lang_id)
    SELECT id, 'la', 'ru' FROM translator_provider WHERE title = 'Scriptorium';
INSERT INTO translator(service_id, src_lang_id, dest_lang_id)
    SELECT id, 'pl', 'ru' FROM translator_provider WHERE title = 'Scriptorium';
INSERT INTO translator(service_id, src_lang_id, dest_lang_id)
    SELECT id, 'sv', 'ru' FROM translator_provider WHERE title = 'Scriptorium';
INSERT INTO translator(service_id, src_lang_id, dest_lang_id)
    SELECT id, 'zh', 'ru' FROM translator_provider WHERE title = 'Scriptorium';
