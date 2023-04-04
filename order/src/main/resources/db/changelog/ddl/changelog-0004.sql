alter table if exists "order"
add column order_status varchar(255),
add column payment_status varchar(255),
add column coupon_code varchar(255),
add column shipping_address_id bigserial not null,
add column billing_address_id bigserial not null,
add column email varchar(255),
drop column address;

alter table if exists order_item
add column product_name varchar(255),
add column product_price varchar(255),
add column number_item decimal(19,2),
add column tax_amount decimal(19,2),
add column tax_percent decimal(19,2),
drop column price;
