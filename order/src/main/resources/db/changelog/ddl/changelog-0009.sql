alter table if exists order_address
add column district_name varchar(255),
add column state_or_province_name varchar(255),
add column country_name varchar(255);

alter table if exists "order"
add column payment_status varchar(255);