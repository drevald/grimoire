-- Migration: Add enabled flag to translator_provider, disable unused providers, add Yandex Translate.
-- Apply: docker exec -i grimoire-postgres-1 psql -U grimoire -d grimoire < src/setup/9-add-enabled-yandex.sql

-- ── Add enabled column ────────────────────────────────────────────────────────
ALTER TABLE translator_provider ADD COLUMN IF NOT EXISTS enabled boolean NOT NULL DEFAULT TRUE;

-- ── Disable all providers except MyMemory and Scriptorium ─────────────────────
UPDATE translator_provider SET enabled = FALSE
    WHERE title NOT IN ('MyMemory', 'Scriptorium');

-- ── Yandex Translate API v2 ────────────────────────────────────────────────────
-- Sign up at console.cloud.yandex.com → Translate API (free tier: 1M chars/month)
-- 1. Create a service account with role ai.translate.user
-- 2. Create an API key for that service account
-- 3. Replace YOUR_YANDEX_KEY below
-- Optional: add "folderId":"YOUR_FOLDER_ID" to request_body if your account requires it
INSERT INTO translator_provider(title, host, method, req_pattern, res_pattern, request_body, content_type, charset, headers, enabled)
    VALUES (
        'Yandex Translate',
        'translate.api.cloud.yandex.net',
        'POST',
        'https://translate.api.cloud.yandex.net/translate/v2/translate',
        '{"translations":[{"text":"{0}","detectedLanguageCode"',
        '{"texts":["{0}"],"sourceLanguageCode":"{1}","targetLanguageCode":"{2}"}',
        'application/json',
        'UTF-8',
        'Authorization: Api-Key YOUR_YANDEX_KEY',
        TRUE
    );

-- ── Yandex language pairs (→ ru, matching Scriptorium) ───────────────────────
DO $$
DECLARE
    yandex_id BIGINT;
BEGIN
    SELECT id INTO yandex_id FROM translator_provider WHERE title = 'Yandex Translate';

    INSERT INTO translator(service_id, src_lang_id, dest_lang_id) VALUES
        (yandex_id, 'ar', 'ru'),
        (yandex_id, 'da', 'ru'),
        (yandex_id, 'de', 'ru'),
        (yandex_id, 'el', 'ru'),
        (yandex_id, 'es', 'ru'),
        (yandex_id, 'fi', 'ru'),
        (yandex_id, 'fr', 'ru'),
        (yandex_id, 'is', 'ru'),
        (yandex_id, 'ja', 'ru'),
        (yandex_id, 'la', 'ru'),
        (yandex_id, 'pl', 'ru'),
        (yandex_id, 'sv', 'ru'),
        (yandex_id, 'zh', 'ru')
    ON CONFLICT DO NOTHING;
END $$;
