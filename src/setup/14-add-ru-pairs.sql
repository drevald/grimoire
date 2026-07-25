-- Migration: Add → ru (Russian) translator pairs for all enabled providers.
-- Mirrors the same source languages already configured for → en.
-- Apply: type src\setup\14-add-ru-pairs.sql | ssh denis@dobby "docker exec -i grimoire-postgres-1 psql -U denis -d grimoire -p 5434"

DO $$
DECLARE
    yandex_id  BIGINT;
    mm_id      BIGINT;
    libre_id   BIGINT;
    deepl_id   BIGINT;
    azure_id   BIGINT;
BEGIN
    SELECT id INTO yandex_id FROM translator_provider WHERE title = 'Yandex Translate';
    SELECT id INTO mm_id     FROM translator_provider WHERE title = 'MyMemory';
    SELECT id INTO libre_id  FROM translator_provider WHERE title = 'LibreTranslate';
    SELECT id INTO deepl_id  FROM translator_provider WHERE title = 'DeepL';
    SELECT id INTO azure_id  FROM translator_provider WHERE title = 'Azure Translator';

    -- Yandex Translate → ru
    IF yandex_id IS NOT NULL THEN
        INSERT INTO translator(service_id, src_lang_id, dest_lang_id) VALUES
            (yandex_id, 'ar', 'ru'), (yandex_id, 'da', 'ru'), (yandex_id, 'de', 'ru'),
            (yandex_id, 'el', 'ru'), (yandex_id, 'es', 'ru'), (yandex_id, 'fi', 'ru'),
            (yandex_id, 'fr', 'ru'), (yandex_id, 'is', 'ru'), (yandex_id, 'ja', 'ru'),
            (yandex_id, 'la', 'ru'), (yandex_id, 'pl', 'ru'), (yandex_id, 'sv', 'ru'),
            (yandex_id, 'zh', 'ru')
        ON CONFLICT DO NOTHING;
    END IF;

    -- MyMemory → ru
    IF mm_id IS NOT NULL THEN
        INSERT INTO translator(service_id, src_lang_id, dest_lang_id) VALUES
            (mm_id, 'ar', 'ru'), (mm_id, 'da', 'ru'), (mm_id, 'de', 'ru'),
            (mm_id, 'el', 'ru'), (mm_id, 'es', 'ru'), (mm_id, 'fi', 'ru'),
            (mm_id, 'fr', 'ru'), (mm_id, 'is', 'ru'), (mm_id, 'ja', 'ru'),
            (mm_id, 'la', 'ru'), (mm_id, 'pl', 'ru'), (mm_id, 'sv', 'ru'),
            (mm_id, 'zh', 'ru')
        ON CONFLICT DO NOTHING;
    END IF;

    -- LibreTranslate → ru
    IF libre_id IS NOT NULL THEN
        INSERT INTO translator(service_id, src_lang_id, dest_lang_id) VALUES
            (libre_id, 'ar', 'ru'), (libre_id, 'da', 'ru'), (libre_id, 'de', 'ru'),
            (libre_id, 'el', 'ru'), (libre_id, 'es', 'ru'), (libre_id, 'fi', 'ru'),
            (libre_id, 'fr', 'ru'), (libre_id, 'is', 'ru'), (libre_id, 'ja', 'ru'),
            (libre_id, 'la', 'ru'), (libre_id, 'pl', 'ru'), (libre_id, 'sv', 'ru'),
            (libre_id, 'zh', 'ru')
        ON CONFLICT DO NOTHING;
    END IF;

    -- DeepL → ru
    IF deepl_id IS NOT NULL THEN
        INSERT INTO translator(service_id, src_lang_id, dest_lang_id) VALUES
            (deepl_id, 'ar', 'ru'), (deepl_id, 'da', 'ru'), (deepl_id, 'de', 'ru'),
            (deepl_id, 'el', 'ru'), (deepl_id, 'es', 'ru'), (deepl_id, 'fi', 'ru'),
            (deepl_id, 'fr', 'ru'), (deepl_id, 'ja', 'ru'), (deepl_id, 'la', 'ru'),
            (deepl_id, 'pl', 'ru'), (deepl_id, 'sv', 'ru'), (deepl_id, 'zh', 'ru')
        ON CONFLICT DO NOTHING;
    END IF;

    -- Azure Translator → ru
    IF azure_id IS NOT NULL THEN
        INSERT INTO translator(service_id, src_lang_id, dest_lang_id) VALUES
            (azure_id, 'ar', 'ru'), (azure_id, 'da', 'ru'), (azure_id, 'de', 'ru'),
            (azure_id, 'el', 'ru'), (azure_id, 'es', 'ru'), (azure_id, 'fi', 'ru'),
            (azure_id, 'fr', 'ru'), (azure_id, 'is', 'ru'), (azure_id, 'ja', 'ru'),
            (azure_id, 'la', 'ru'), (azure_id, 'pl', 'ru'), (azure_id, 'sv', 'ru'),
            (azure_id, 'zh', 'ru')
        ON CONFLICT DO NOTHING;
    END IF;
END $$;
