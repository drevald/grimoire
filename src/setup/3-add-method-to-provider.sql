-- ============================================================================
-- Migration: Add HTTP method support to translator_provider
-- Date: 2025-11-14
-- Description: Adds 'method', 'content_type', and 'charset' columns to allow
--              POST requests with configurable content types for translation APIs
--              Also converts ID column to auto-increment (BIGSERIAL)
-- ============================================================================

-- Convert ID to auto-increment if not already
-- Create sequence if it doesn't exist
CREATE SEQUENCE IF NOT EXISTS translator_provider_id_seq;

-- Set the sequence ownership and default
ALTER TABLE translator_provider ALTER COLUMN id SET DEFAULT nextval('translator_provider_id_seq');
ALTER SEQUENCE translator_provider_id_seq OWNED BY translator_provider.id;

-- Set sequence to current max ID + 1 to avoid conflicts
SELECT setval('translator_provider_id_seq', COALESCE((SELECT MAX(id) FROM translator_provider), 0) + 1, false);

-- Add method column to translator_provider table
-- This allows specifying GET or POST for translation API requests
ALTER TABLE translator_provider
ADD COLUMN IF NOT EXISTS method VARCHAR(10) DEFAULT 'GET';

-- Add content_type column for POST requests
-- This allows specifying Content-Type header (e.g., application/json, application/x-www-form-urlencoded)
ALTER TABLE translator_provider
ADD COLUMN IF NOT EXISTS content_type VARCHAR(100) DEFAULT 'application/json';

-- Add charset column for POST requests
-- This allows specifying character encoding (e.g., UTF-8, ISO-8859-1)
ALTER TABLE translator_provider
ADD COLUMN IF NOT EXISTS charset VARCHAR(20) DEFAULT 'UTF-8';

-- Add headers column for custom HTTP headers
-- This allows specifying custom headers like API keys, authorization tokens, etc.
-- Format: "Header-Name: value\nAnother-Header: value"
ALTER TABLE translator_provider
ADD COLUMN IF NOT EXISTS headers VARCHAR(1000);

-- Add request_body column for POST request body template
-- This is a dedicated field for POST request bodies, making the configuration clearer
-- Previously, resPattern was overloaded for both URL templates (GET) and request bodies (POST)
ALTER TABLE translator_provider
ADD COLUMN IF NOT EXISTS request_body VARCHAR(1000);

-- Migrate existing POST providers: copy resPattern to request_body for POST methods
-- This ensures backward compatibility with existing POST configurations
UPDATE translator_provider
SET request_body = res_pattern
WHERE method = 'POST' AND request_body IS NULL;

-- IMPORTANT: Swap req_pattern and res_pattern to fix naming confusion
-- Current (wrong): req_pattern = response parser, res_pattern = request URL
-- Desired (correct): req_pattern = request URL, res_pattern = response parser
-- We need to swap the values in these columns for all existing records

-- Create a temporary column to hold values during swap
ALTER TABLE translator_provider ADD COLUMN IF NOT EXISTS temp_pattern VARCHAR(255);

-- Step 1: Copy req_pattern to temp
UPDATE translator_provider SET temp_pattern = req_pattern;

-- Step 2: Copy res_pattern to req_pattern
UPDATE translator_provider SET req_pattern = res_pattern;

-- Step 3: Copy temp to res_pattern
UPDATE translator_provider SET res_pattern = temp_pattern;

-- Step 4: Drop temporary column
ALTER TABLE translator_provider DROP COLUMN IF EXISTS temp_pattern;

-- Set default to GET for existing providers (backward compatibility)
-- This ensures all existing providers continue to work as before
UPDATE translator_provider
SET method = 'GET'
WHERE method IS NULL OR method = '';

UPDATE translator_provider
SET content_type = 'application/json'
WHERE content_type IS NULL OR content_type = '';

UPDATE translator_provider
SET charset = 'UTF-8'
WHERE charset IS NULL OR charset = '';

-- Add column comments for documentation
COMMENT ON COLUMN translator_provider.method IS 'HTTP method for translation requests: GET or POST.
For GET: req_pattern = URL template, res_pattern = response parser
For POST: host = API endpoint URL, req_pattern = URL (can include query params), request_body = request body template, res_pattern = response parser';

COMMENT ON COLUMN translator_provider.content_type IS 'Content-Type header for POST requests (e.g., application/json, application/x-www-form-urlencoded, text/plain)';

COMMENT ON COLUMN translator_provider.charset IS 'Character encoding for POST requests (e.g., UTF-8, ISO-8859-1, Windows-1251)';

COMMENT ON COLUMN translator_provider.headers IS 'Custom HTTP headers for API requests (e.g., API keys, authorization tokens). Format: "Header-Name: value\nAnother-Header: value". Lines starting with # are treated as comments.';

COMMENT ON COLUMN translator_provider.request_body IS 'Request body template for POST requests. Supports placeholders: {0} = text to translate, {1} = source language, {2} = target language. Example: {"text":"{0}","source":"{1}","target":"{2}"}';

COMMENT ON COLUMN translator_provider.req_pattern IS 'Request URL pattern template. For GET: full URL with placeholders. For POST: endpoint URL (can include query params). Placeholders: {0} = text, {1} = source lang, {2} = target lang';

COMMENT ON COLUMN translator_provider.res_pattern IS 'Response parser pattern. MessageFormat pattern to extract translation from API response. Usually {0} to extract the first element from the response.';

-- Verify the migration
SELECT
    id,
    title,
    host,
    method,
    content_type,
    charset,
    CASE WHEN headers IS NOT NULL THEN 'Has headers' ELSE 'No headers' END as headers_status,
    CASE
        WHEN method IS NULL THEN 'WARNING: NULL method'
        WHEN method = '' THEN 'WARNING: Empty method'
        WHEN method NOT IN ('GET', 'POST') THEN 'WARNING: Invalid method value'
        ELSE 'OK'
    END as status
FROM translator_provider;

-- ============================================================================
-- End of migration
-- ============================================================================
