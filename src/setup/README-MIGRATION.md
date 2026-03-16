# Database Migration Guide

## Migration 3: Add HTTP Method Support to Translator Provider

### Overview
This migration adds support for POST requests to translation providers, allowing integration with modern translation APIs that require POST requests with JSON payloads.

### Files
- `3-add-method-to-provider.sql` - Migration script to add `method` column

### What Changed
1. Added `method` VARCHAR(10) column to `translator_provider` table (default: 'GET')
2. Added `content_type` VARCHAR(100) column (default: 'application/json')
3. Added `charset` VARCHAR(20) column (default: 'UTF-8')
4. All defaults ensure backward compatibility - existing providers continue to work without changes

### Applying the Migration

#### Development/Local Database
```bash
psql -h dobby -p 5434 -U denis -d grimoire -f src/setup/3-add-method-to-provider.sql
```

#### Production Database
```bash
psql -h <prod-host> -p <prod-port> -U <prod-user> -d grimoire -f src/setup/3-add-method-to-provider.sql
```

### Pattern Configuration

#### For GET Requests (existing behavior)
- **host**: Not used (can be empty or contain base URL)
- **resPattern**: Full URL template, e.g., `http://api.example.com/translate?text={0}&from={1}&to={2}`
- **reqPattern**: Response parser, e.g., `{0}` to extract the translation
- **method**: `GET`

**Example:**
```sql
INSERT INTO translator_provider (id, title, host, req_pattern, res_pattern, method)
VALUES (1, 'OldAPI', '', '{0}', 'http://api.example.com/translate?text={0}&from={1}&to={2}', 'GET');
```

#### For POST Requests (new)
- **host**: API endpoint URL, e.g., `https://api.example.com/translate`
- **resPattern**: Request body template, e.g., `{"text":"{0}","source":"{1}","target":"{2}"}`
- **reqPattern**: Response parser, e.g., `{0}` to extract the translation
- **method**: `POST`
- **content_type**: MIME type for the request (e.g., `application/json`, `application/x-www-form-urlencoded`, `text/plain`)
- **charset**: Character encoding (e.g., `UTF-8`, `ISO-8859-1`, `Windows-1251`)

**Example (JSON API):**
```sql
INSERT INTO translator_provider (id, title, host, req_pattern, res_pattern, method, content_type, charset)
VALUES (2, 'ModernAPI', 'https://api.example.com/translate', '{0}', '{"text":"{0}","source":"{1}","target":"{2}"}', 'POST', 'application/json', 'UTF-8');
```

**Example (Form-encoded API):**
```sql
INSERT INTO translator_provider (id, title, host, req_pattern, res_pattern, method, content_type, charset)
VALUES (3, 'FormAPI', 'https://api.example.com/translate', '{0}', 'text={0}&from={1}&to={2}', 'POST', 'application/x-www-form-urlencoded', 'UTF-8');
```

**Example (Plain text with custom charset):**
```sql
INSERT INTO translator_provider (id, title, host, req_pattern, res_pattern, method, content_type, charset)
VALUES (4, 'PlainAPI', 'https://api.example.com/translate', '{0}', '{0}', 'POST', 'text/plain', 'Windows-1251');
```

### Placeholders
- `{0}` = URL-encoded text to translate
- `{1}` = Source language code (e.g., 'en', 'pl')
- `{2}` = Target language code (e.g., 'ru', 'de')

### Verification
After applying the migration, verify it worked:
```sql
SELECT id, title, host, method,
    CASE
        WHEN method IS NULL THEN 'ERROR: NULL method'
        WHEN method = '' THEN 'ERROR: Empty method'
        WHEN method NOT IN ('GET', 'POST') THEN 'ERROR: Invalid method'
        ELSE 'OK'
    END as status
FROM translator_provider;
```

### Rollback
If you need to rollback this migration:
```sql
ALTER TABLE translator_provider DROP COLUMN IF EXISTS method;
ALTER TABLE translator_provider DROP COLUMN IF EXISTS content_type;
ALTER TABLE translator_provider DROP COLUMN IF EXISTS charset;
```
**Note:** This will break the POST provider feature. Ensure all providers are configured as GET before rolling back.

### Testing
After migration, test both GET and POST providers:
1. Create a test provider with POST method
2. Trigger a translation job
3. Check logs for "POST request to:" and "POST response:" debug messages
4. Verify translations are working correctly
