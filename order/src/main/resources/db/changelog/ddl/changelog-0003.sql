alter table if exists "order"
add column created_by varchar(255),
add column created_on timestamp(6),
add column last_modified_by varchar(255),
add column last_modified_on timestamp(6);