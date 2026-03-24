-- Migration: Ensure Yandex Translate has pl→en pair.
-- Apply: type src\setup\12-yandex-pl-en.sql | ssh denis@dobby "docker exec -i grimoire-postgres-1 psql -U denis -d grimoire -p 5434"

DO $$
DECLARE
    yandex_id BIGINT;
BEGIN
    SELECT id INTO yandex_id FROM translator_provider WHERE title = 'Yandex Translate';

    INSERT INTO translator(service_id, src_lang_id, dest_lang_id) VALUES
        (yandex_id, 'pl', 'en')
    ON CONFLICT DO NOTHING;
END $$;
