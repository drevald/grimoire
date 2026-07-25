-- Migration: Fix translation table unique constraint.
-- word_id alone was UNIQUE, allowing only one translation per word regardless of translator.
-- Replace with UNIQUE(word_id, translator_id) to allow one translation per word per translator.
-- Apply: type src\setup\16-fix-translation-unique.sql | ssh denis@dobby "docker exec -i grimoire-postgres-1 psql -U denis -d grimoire -p 5434"

ALTER TABLE translation DROP CONSTRAINT IF EXISTS translation_word_id_key;
ALTER TABLE translation ADD CONSTRAINT uq_translation_word_translator UNIQUE (word_id, translator_id);
