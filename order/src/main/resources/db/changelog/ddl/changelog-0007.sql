alter table if exists "order"
drop column payment_status,
drop column payment_method,
add column payment_id bigint;