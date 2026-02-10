ALTER TABLE parts ALTER COLUMN description DROP NOT NULL;
UPDATE parts SET description = NULL WHERE description = '';
