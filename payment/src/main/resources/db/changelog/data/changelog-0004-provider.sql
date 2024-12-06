--liquibase formatted sql

--changeset duylv:#1171-2 - update payment provider
UPDATE payment_provider
SET media_id = 82
WHERE id = 'PAYPAL';

UPDATE payment_provider
SET media_id = 81
WHERE id = 'COD';