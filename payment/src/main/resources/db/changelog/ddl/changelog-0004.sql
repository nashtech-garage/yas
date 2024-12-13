--changeset duylv:#1171-1
ALTER TABLE IF EXISTS "payment_provider"
ADD COLUMN media_id integer,
ADD COLUMN version integer DEFAULT 0 NOT NULL,
ADD COLUMN created_on timestamp(6) DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN created_by varchar(255) DEFAULT 'admin',
ADD COLUMN last_modified_on timestamp(6) DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN last_modified_by varchar(255) DEFAULT 'admin';

ALTER TABLE IF EXISTS "payment_provider" RENAME COLUMN is_enabled TO enabled;