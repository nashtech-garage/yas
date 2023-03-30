--liquibase formatted sql

--changeset nguyenvanhadncntt:issue-474-1
create table country (id bigserial not null, name varchar(255) not null);
create table state_or_province (id bigserial not null, name varchar(255) not null);
create table district (id bigserial not null, name varchar(255) not null);
create table address (id bigserial not null, streetName varchar(255) not null, no varchar(255) not null);

--changeset dieunguyen:issue-474-2
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

alter table if exists country alter column name type varchar(450);
alter table if exists country add column code3 varchar(450) not null;
alter table if exists country add column is_billing_enabled boolean;
alter table if exists country add column is_shipping_enabled boolean;
alter table if exists country add column is_city_enabled boolean;
alter table if exists country add column is_zip_code_enabled boolean;
alter table if exists country add column is_district_enabled boolean;
alter table if exists country add primary key (id);

alter table if exists state_or_province alter column name type varchar(450);
alter table if exists state_or_province add column code varchar(450);
alter table if exists state_or_province add column type varchar(450);
alter table if exists state_or_province add column country_id bigserial not null;

alter table if exists district add primary key (id);
alter table if exists state_or_province add primary key (id);
alter table if exists district alter column name type varchar(450);
alter table if exists district add column type varchar(450);
alter table if exists district add column location varchar(450);
alter table if exists district add column state_or_province_id bigserial not null;

alter table if exists address add column created_by varchar(255);
alter table if exists address add column created_on timestamp(6);
alter table if exists address add column last_modified_by varchar(255);
alter table if exists address add column last_modified_on timestamp(6);
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

alter table if exists state_or_province add constraint FK2y94svpmqttx80mshyny85wqr foreign key (country_id) references country;
alter table if exists district add constraint FK2y94svpmqttx80mshyny85wqr foreign key (state_or_province_id) references state_or_province;
alter table if exists address add constraint FK2y94svpmqttx80mshyny85wqr foreign key (state_or_province_id) references state_or_province;
alter table if exists address add constraint FK2y94svpmqttx80mshyny85wqa foreign key (country_id) references country;
alter table if exists address add constraint FK2y94svpmqttx80mshyny85wqs foreign key (district_id) references district;

--changeset dieunguyen:issue-474-5
alter table if exists country add column code2 char(3);
alter table if exists country alter column code3 type char(3);
alter table if exists state_or_province alter column code drop not null;
