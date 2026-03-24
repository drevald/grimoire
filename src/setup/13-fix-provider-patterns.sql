-- Migration: Fix MyMemory pipe in URL and Yandex res_pattern.
-- Apply: type src\setup\13-fix-provider-patterns.sql | ssh denis@dobby "docker exec -i grimoire-postgres-1 psql -U denis -d grimoire -p 5434"

-- MyMemory: | is invalid in a URI — encode as %7C
UPDATE translator_provider
    SET req_pattern = 'https://api.mymemory.translated.net/get?q={0}&langpair={1}%7C{2}'
    WHERE title = 'MyMemory';

-- Yandex: drop the ",detectedLanguageCode" suffix — Yandex omits it when source lang is explicit
UPDATE translator_provider
    SET res_pattern = '{"translations":[{"text":"{0}"'
    WHERE title = 'Yandex Translate';
