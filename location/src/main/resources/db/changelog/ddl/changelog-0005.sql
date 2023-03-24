--liquibase formatted sql

--changeset dieunguyen:issue-001
alter table if exists address drop column streetname;
alter table if exists address drop column no;
alter table if exists address add primary key (id);
alter table if exists address add column contact_name varchar(450);
alter table if exists address add column phone varchar(25);
alter table if exists address add column address_line_1 varchar(450);
alter table if exists address add column address_line_2 varchar(450);
alter table if exists address add column city varchar(450);
alter table if exists address add column zip_code varchar(25);
alter table if exists address add column district_id bigserial not null;
alter table if exists address add column state_or_province_id bigserial not null;
alter table if exists address add column country_id bigserial not null;

alter table if exists address add constraint FK2y94svpmqttx80mshyny85wqr foreign key (state_or_province_id) references state_or_province;
alter table if exists address add constraint FK2y94svpmqttx80mshyny85wqa foreign key (country_id) references country;
alter table if exists address add constraint FK2y94svpmqttx80mshyny85wqs foreign key (district_id) references district;

--changeset dieunguyen:issue-002
alter table if exists address add column created_by varchar(255);
alter table if exists address add column created_on timestamp(6);
alter table if exists address add column last_modified_by varchar(255);
alter table if exists address add column last_modified_on timestamp(6);

--changeset dieunguyen:issue-003
alter table if exists country add column created_by varchar(255);
alter table if exists country add column created_on timestamp(6);
alter table if exists country add column last_modified_by varchar(255);
alter table if exists country add column last_modified_on timestamp(6);
alter table if exists state_or_province add column created_by varchar(255);
alter table if exists state_or_province add column created_on timestamp(6);
alter table if exists state_or_province add column last_modified_by varchar(255);
alter table if exists state_or_province add column last_modified_on timestamp(6);
alter table if exists district add column created_by varchar(255);
alter table if exists district add column created_on timestamp(6);
alter table if exists district add column last_modified_by varchar(255);
alter table if exists district add column last_modified_on timestamp(6);
