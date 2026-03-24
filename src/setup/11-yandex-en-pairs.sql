-- Migration: Add Yandex Translate language pairs → en (English).
-- Apply: type src\setup\11-yandex-en-pairs.sql | ssh denis@dobby "docker exec -i grimoire-postgres-1 psql -U denis -d grimoire -p 5434"

DO $$
DECLARE
    yandex_id BIGINT;
BEGIN
    SELECT id INTO yandex_id FROM translator_provider WHERE title = 'Yandex Translate';

    INSERT INTO translator(service_id, src_lang_id, dest_lang_id) VALUES
        (yandex_id, 'ar', 'en'),
        (yandex_id, 'da', 'en'),
        (yandex_id, 'de', 'en'),
        (yandex_id, 'el', 'en'),
        (yandex_id, 'es', 'en'),
        (yandex_id, 'fi', 'en'),
        (yandex_id, 'fr', 'en'),
        (yandex_id, 'is', 'en'),
        (yandex_id, 'ja', 'en'),
        (yandex_id, 'la', 'en'),
        (yandex_id, 'pl', 'en'),
        (yandex_id, 'ru', 'en'),
        (yandex_id, 'sv', 'en'),
        (yandex_id, 'zh', 'en')
    ON CONFLICT DO NOTHING;
END $$;
