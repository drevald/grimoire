-- Migration: Add LibreTranslate, DeepL, and Azure Translator providers
-- Apply to existing DB: docker exec -i grimoire-postgres-1 psql -U grimoire -d grimoire < src/setup/7-add-libre-deepl-azure.sql
--
-- BEFORE USE — set your API keys:
--   LibreTranslate: replace YOUR_LIBRE_KEY in request_body (or remove api_key field if self-hosted without auth)
--   DeepL:          replace YOUR_DEEPL_KEY in headers
--   Azure:          replace YOUR_AZURE_KEY and YOUR_AZURE_REGION in headers
--
-- Response pattern notes ({0}=translated text, higher indices capture surrounding junk):
--   LibreTranslate: {"translatedText":"hello"} → pattern extracts {0} between quotes
--   DeepL:          {"translations":[{..."text":"hello"...}]} → {1}"text":"{0}"{2}
--   Azure:          [{"translations":[{"text":"hello","to":"en"}]}] → {1}"text":"{0}","to"{2}

-- ── LibreTranslate ────────────────────────────────────────────────────────────
-- Public instance: https://libretranslate.com (requires free API key from libretranslate.com)
-- Self-hosted (no key needed): change req_pattern host and remove api_key from request_body
INSERT INTO translator_provider(title, host, method, req_pattern, res_pattern, request_body, content_type, charset)
    VALUES (
        'LibreTranslate',
        'libretranslate.com',
        'POST',
        'https://libretranslate.com/translate',
        '{"translatedText":"{0}"}',
        '{"q":"{0}","source":"{1}","target":"{2}","format":"text","api_key":"YOUR_LIBRE_KEY"}',
        'application/json',
        'UTF-8'
    );

-- ── DeepL Free ────────────────────────────────────────────────────────────────
-- Sign up at deepl.com/pro-api (free tier: 500k chars/month)
-- For paid plan change host to api.deepl.com
INSERT INTO translator_provider(title, host, method, req_pattern, res_pattern, request_body, content_type, charset, headers)
    VALUES (
        'DeepL',
        'api-free.deepl.com',
        'POST',
        'https://api-free.deepl.com/v2/translate',
        '{1}"text":"{0}"{2}',
        '{"text":["{0}"],"source_lang":"{1}","target_lang":"{2}"}',
        'application/json',
        'UTF-8',
        'Authorization: DeepL-Auth-Key YOUR_DEEPL_KEY'
    );

-- ── Azure Translator ──────────────────────────────────────────────────────────
-- Sign up at portal.azure.com → Cognitive Services → Translator
-- Free tier: 2M chars/month. Region examples: eastus, westeurope, etc.
INSERT INTO translator_provider(title, host, method, req_pattern, res_pattern, request_body, content_type, charset, headers)
    VALUES (
        'Azure Translator',
        'api.cognitive.microsofttranslator.com',
        'POST',
        'https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&from={1}&to={2}',
        '{1}"text":"{0}","to"{2}',
        '[{"Text":"{0}"}]',
        'application/json',
        'UTF-8',
        'Ocp-Apim-Subscription-Key: YOUR_AZURE_KEY
Ocp-Apim-Subscription-Region: YOUR_AZURE_REGION'
    );

-- ── Language pairs → English ──────────────────────────────────────────────────
-- Add translators for common source languages to English for all three providers.
-- Uses INSERT ... SELECT to get the provider IDs dynamically.

DO $$
DECLARE
    libre_id BIGINT;
    deepl_id BIGINT;
    azure_id BIGINT;
BEGIN
    SELECT id INTO libre_id FROM translator_provider WHERE title = 'LibreTranslate';
    SELECT id INTO deepl_id FROM translator_provider WHERE title = 'DeepL';
    SELECT id INTO azure_id FROM translator_provider WHERE title = 'Azure Translator';

    -- LibreTranslate pairs (→ en)
    INSERT INTO translator(service_id, src_lang_id, dest_lang_id) VALUES
        (libre_id, 'ar', 'en'), (libre_id, 'da', 'en'), (libre_id, 'de', 'en'),
        (libre_id, 'el', 'en'), (libre_id, 'es', 'en'), (libre_id, 'fi', 'en'),
        (libre_id, 'fr', 'en'), (libre_id, 'is', 'en'), (libre_id, 'ja', 'en'),
        (libre_id, 'la', 'en'), (libre_id, 'pl', 'en'), (libre_id, 'ru', 'en'),
        (libre_id, 'sv', 'en'), (libre_id, 'zh', 'en')
    ON CONFLICT DO NOTHING;

    -- DeepL pairs (→ en) — DeepL uses "EN" uppercase but accepts lowercase input
    INSERT INTO translator(service_id, src_lang_id, dest_lang_id) VALUES
        (deepl_id, 'ar', 'en'), (deepl_id, 'da', 'en'), (deepl_id, 'de', 'en'),
        (deepl_id, 'el', 'en'), (deepl_id, 'es', 'en'), (deepl_id, 'fi', 'en'),
        (deepl_id, 'fr', 'en'), (deepl_id, 'ja', 'en'), (deepl_id, 'la', 'en'),
        (deepl_id, 'pl', 'en'), (deepl_id, 'ru', 'en'), (deepl_id, 'sv', 'en'),
        (deepl_id, 'zh', 'en')
    ON CONFLICT DO NOTHING;

    -- Azure Translator pairs (→ en)
    INSERT INTO translator(service_id, src_lang_id, dest_lang_id) VALUES
        (azure_id, 'ar', 'en'), (azure_id, 'da', 'en'), (azure_id, 'de', 'en'),
        (azure_id, 'el', 'en'), (azure_id, 'es', 'en'), (azure_id, 'fi', 'en'),
        (azure_id, 'fr', 'en'), (azure_id, 'is', 'en'), (azure_id, 'ja', 'en'),
        (azure_id, 'la', 'en'), (azure_id, 'pl', 'en'), (azure_id, 'ru', 'en'),
        (azure_id, 'sv', 'en'), (azure_id, 'zh', 'en')
    ON CONFLICT DO NOTHING;
END $$;
