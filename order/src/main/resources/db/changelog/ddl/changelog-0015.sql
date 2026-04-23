ALTER TABLE IF EXISTS "checkout"
ADD COLUMN progress VARCHAR(255),
ADD COLUMN customer_id BIGSERIAL,
ADD COLUMN shipment_method_id VARCHAR(255),
ADD COLUMN payment_method_id VARCHAR(255),
ADD COLUMN shipping_address_id BIGSERIAL,
ADD COLUMN last_error JSONB,
ADD COLUMN attributes JSONB,
ADD COLUMN total_amount DECIMAL(19,2),
ADD COLUMN total_shipment_fee DECIMAL(19,2),
ADD COLUMN total_shipment_tax DECIMAL(19,2),
ADD COLUMN total_tax DECIMAL(19,2),
ADD COLUMN total_discount_amount DECIMAL(19,2);
ALTER TABLE "checkout"
RENAME COLUMN coupon_code TO promotion_code;
ALTER TABLE "checkout"
RENAME COLUMN checkout_state TO status;

ALTER TABLE IF EXISTS "order"
ADD COLUMN progress VARCHAR(255),
ADD COLUMN customer_id BIGSERIAL,
ADD COLUMN payment_method_id VARCHAR(255),
ADD COLUMN last_error JSONB,
ADD COLUMN attributes JSONB,
ADD COLUMN total_shipment_tax DECIMAL(19,2);
ALTER TABLE IF EXISTS "order"
RENAME COLUMN tax TO total_tax;
ALTER TABLE IF EXISTS "order"
RENAME COLUMN discount TO total_discount_amount;
ALTER TABLE IF EXISTS "order"
RENAME COLUMN total_price TO total_amount;
ALTER TABLE IF EXISTS "order"
RENAME COLUMN delivery_fee TO total_shipment_fee;
ALTER TABLE IF EXISTS "order"
RENAME COLUMN delivery_method TO shipment_method_id;
ALTER TABLE IF EXISTS "order"
RENAME COLUMN delivery_status TO shipment_status;
ALTER TABLE IF EXISTS "order"
RENAME COLUMN order_status TO status;
ALTER TABLE IF EXISTS "order"
RENAME COLUMN coupon_code TO promotion_code;

ALTER TABLE IF EXISTS "checkout_item"
ADD COLUMN created_by varchar(255),
ADD COLUMN created_on timestamp(6),
ADD COLUMN last_modified_by varchar(255),
ADD COLUMN last_modified_on timestamp(6),
ADD COLUMN shipment_fee DECIMAL(19,2),
ADD COLUMN shipment_tax DECIMAL(19,2);
ALTER TABLE IF EXISTS "checkout_item"
RENAME COLUMN tax_amount TO tax;
ALTER TABLE IF EXISTS "checkout_item"
RENAME COLUMN product_name TO name;
ALTER TABLE IF EXISTS "checkout_item"
RENAME COLUMN product_price TO price;
ALTER TABLE IF EXISTS "checkout_item"
RENAME COLUMN note TO description;

ALTER TABLE IF EXISTS "order_item"
ADD COLUMN created_by varchar(255),
ADD COLUMN created_on timestamp(6),
ADD COLUMN last_modified_by varchar(255),
ADD COLUMN last_modified_on timestamp(6),
ADD COLUMN shipment_fee DECIMAL(19,2),
ADD COLUMN shipment_tax DECIMAL(19,2),
ADD COLUMN status varchar(255),
ADD COLUMN processing_state JSONB;
ALTER TABLE IF EXISTS "order_item"
RENAME COLUMN note TO description;
ALTER TABLE IF EXISTS "order_item"
RENAME COLUMN product_name TO name;
ALTER TABLE IF EXISTS "order_item"
RENAME COLUMN product_price TO price;

ALTER TABLE "checkout"
ALTER COLUMN customer_id DROP NOT NULL;
ALTER TABLE "checkout"
ALTER COLUMN shipping_address_id DROP NOT NULL;

ALTER TABLE "order"
ALTER COLUMN customer_id DROP NOT NULL;