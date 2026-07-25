-- Add audio_pattern column to translator_provider.
-- Pattern placeholders: {0}=word, {1}=srcLang
-- Used to fetch audio stream directly (single GET, returns audio bytes or 404).

ALTER TABLE translator_provider ADD COLUMN IF NOT EXISTS audio_pattern varchar(255);

UPDATE translator_provider
    SET audio_pattern = replace(req_pattern, '/api/v1/translate/{1}/{0}/{2}', '/api/v1/entries/{1}/{0}/audio/stream')
    WHERE title = 'Scriptorium';
