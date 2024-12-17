ALTER TABLE IF EXISTS "checkout"
ADD COLUMN checkout_address_id BIGINT,
ADD COLUMN shipment_service_id VARCHAR(255);

ALTER TABLE IF EXISTS "checkout_item"
ADD COLUMN tax_class_id BIGINT,
ADD COLUMN weight DOUBLE PRECISION,
ADD COLUMN dimension_unit VARCHAR(50),
ADD COLUMN length DOUBLE PRECISION,
ADD COLUMN width DOUBLE PRECISION,
ADD COLUMN height DOUBLE PRECISION;

CREATE TABLE IF NOT EXISTS "checkout_address" (
   id bigserial not null,
   contact_name varchar(255) not null,
   phone varchar(255) not null,
   address_line1 varchar(255) not null,
   city varchar(255),
   zip_code varchar(255),
   district_id bigserial,
   district_name varchar(255),
   state_or_province_id bigserial,
   state_or_province_name varchar(255),
   state_or_province_code varchar(10),
   country_id bigserial,
   country_name varchar(255),
   country_code2 varchar(10),
   country_code3 varchar(10),
   type VARCHAR(50),
   checkout_id VARCHAR(255),
   PRIMARY KEY (id)
);

alter table if exists "checkout"
ADD CONSTRAINT FK_CheckoutAddress FOREIGN KEY (checkout_address_id) REFERENCES checkout_address (id);