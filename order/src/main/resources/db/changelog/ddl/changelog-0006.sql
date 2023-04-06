alter table if exists order_address
drop column district,
drop column state_or_province,
drop column country,
add column district_id bigserial,
add column state_or_province_id bigserial,
add column country_id bigserial;