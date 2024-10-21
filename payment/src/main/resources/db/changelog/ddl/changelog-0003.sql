ALTER TABLE IF EXISTS "payment"
ADD COLUMN created_by varchar(255),
ADD COLUMN last_modified_by varchar(255),
ADD COLUMN payment_provider_checkout_id varchar(255);