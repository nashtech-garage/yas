--liquibase formatted sql

--changeset dieunguyen:issue-002
alter table if exists country add column code3 varchar(450) not null;

--changeset dieunguyen:issue-003
alter table if exists country alter column name type varchar(450);

--changeset dieunguyen:issue-004
alter table if exists country add column is_billing_enabled boolean;
alter table if exists country add column is_shipping_enabled boolean;
alter table if exists country add column is_city_enabled boolean;
alter table if exists country add column is_zip_code_enabled boolean;
alter table if exists country add column is_district_enabled boolean;
