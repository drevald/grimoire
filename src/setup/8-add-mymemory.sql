-- Migration: Add MyMemory translation provider
-- Free, no API key required (5000 req/day anonymous, more with free account email param)
-- Apply: docker exec -i grimoire-postgres-1 sh -c "PGPASSWORD=9rommit psql -U denis -d grimoire -h 127.0.0.1 -p 5434" < src/setup/8-add-mymemory.sql
--
-- Response: {"responseData":{"translatedText":"hello","match":1},...}
-- After compactJson: {"responseData":{"translatedText":"hello","match":1},...}
-- res_pattern: {"responseData":{"translatedText":"{0}","match" → extracts "hello"

INSERT INTO translator_provider(title, host, method, req_pattern, res_pattern, content_type, charset)
    VALUES (
        'MyMemory',
        'api.mymemory.translated.net',
        'GET',
        'https://api.mymemory.translated.net/get?q={0}&langpair={1}|{2}',
        '{"responseData":{"translatedText":"{0}","match"',
        'application/json',
        'UTF-8'
    );

DO $$
DECLARE
    mm_id BIGINT;
BEGIN
    SELECT id INTO mm_id FROM translator_provider WHERE title = 'MyMemory';

    INSERT INTO translator(service_id, src_lang_id, dest_lang_id) VALUES
        (mm_id, 'ar', 'en'), (mm_id, 'da', 'en'), (mm_id, 'de', 'en'),
        (mm_id, 'el', 'en'), (mm_id, 'es', 'en'), (mm_id, 'fi', 'en'),
        (mm_id, 'fr', 'en'), (mm_id, 'is', 'en'), (mm_id, 'ja', 'en'),
        (mm_id, 'la', 'en'), (mm_id, 'pl', 'en'), (mm_id, 'ru', 'en'),
        (mm_id, 'sv', 'en'), (mm_id, 'zh', 'en')
    ON CONFLICT DO NOTHING;
END $$;
