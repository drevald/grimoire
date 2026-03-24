-- Migration: Configure Yandex Translate API key and folderId.
-- Apply: docker exec -i grimoire-postgres-1 psql -U denis -d grimoire -p 5434 < src/setup/10-configure-yandex-key.sql

UPDATE translator_provider SET
    headers      = 'Authorization: Api-Key ${YANDEX_API_KEY}',
    request_body = '{"texts":["{0}"],"sourceLanguageCode":"{1}","targetLanguageCode":"{2}","folderId":"YOUR_FOLDER_ID"}',
    res_pattern  = '{"translations":[{"text":"{0}"',
    enabled      = TRUE
WHERE title = 'Yandex Translate';
