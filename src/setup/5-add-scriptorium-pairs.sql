-- Migration: Add missing Scriptorium language pairs.
-- For fresh installs these are already in 4-add-scriptorium-provider.sql.
-- Run this on existing databases that only have the original 6 pairs.

-- Fix http -> https (Scriptorium redirects http with 308, HttpClient 3.x doesn't follow it).
UPDATE translator_provider
    SET req_pattern = replace(req_pattern, 'http://scriptorium.dobby', 'https://scriptorium.dobby')
    WHERE title = 'Scriptorium';

-- Ensure translator.id has a sequence (it was defined as plain bigint, not bigserial).
CREATE SEQUENCE IF NOT EXISTS translator_id_seq;
ALTER TABLE translator ALTER COLUMN id SET DEFAULT nextval('translator_id_seq');
ALTER SEQUENCE translator_id_seq OWNED BY translator.id;
SELECT setval('translator_id_seq', COALESCE((SELECT MAX(id) FROM translator), 0) + 1, false);

-- Remove duplicates before adding the unique constraint.
DELETE FROM translator a USING translator b
    WHERE a.id > b.id
      AND a.service_id = b.service_id
      AND a.src_lang_id = b.src_lang_id
      AND a.dest_lang_id = b.dest_lang_id;

-- Enforce uniqueness on the language pair per provider.
ALTER TABLE translator
    ADD CONSTRAINT uq_translator_pair UNIQUE (service_id, src_lang_id, dest_lang_id);

INSERT INTO translator(service_id, src_lang_id, dest_lang_id)
    SELECT id, 'ar', 'ru' FROM translator_provider WHERE title = 'Scriptorium'
    ON CONFLICT DO NOTHING;
INSERT INTO translator(service_id, src_lang_id, dest_lang_id)
    SELECT id, 'da', 'ru' FROM translator_provider WHERE title = 'Scriptorium'
    ON CONFLICT DO NOTHING;
INSERT INTO translator(service_id, src_lang_id, dest_lang_id)
    SELECT id, 'el', 'ru' FROM translator_provider WHERE title = 'Scriptorium'
    ON CONFLICT DO NOTHING;
INSERT INTO translator(service_id, src_lang_id, dest_lang_id)
    SELECT id, 'es', 'ru' FROM translator_provider WHERE title = 'Scriptorium'
    ON CONFLICT DO NOTHING;
INSERT INTO translator(service_id, src_lang_id, dest_lang_id)
    SELECT id, 'fi', 'ru' FROM translator_provider WHERE title = 'Scriptorium'
    ON CONFLICT DO NOTHING;
INSERT INTO translator(service_id, src_lang_id, dest_lang_id)
    SELECT id, 'ja', 'ru' FROM translator_provider WHERE title = 'Scriptorium'
    ON CONFLICT DO NOTHING;
INSERT INTO translator(service_id, src_lang_id, dest_lang_id)
    SELECT id, 'la', 'ru' FROM translator_provider WHERE title = 'Scriptorium'
    ON CONFLICT DO NOTHING;
INSERT INTO translator(service_id, src_lang_id, dest_lang_id)
    SELECT id, 'zh', 'ru' FROM translator_provider WHERE title = 'Scriptorium'
    ON CONFLICT DO NOTHING;
