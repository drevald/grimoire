-- Widen translation.value from varchar(64) to text.
-- Scriptorium can return long strings; without this the INSERT fails silently.
ALTER TABLE translation ALTER COLUMN value TYPE text;
