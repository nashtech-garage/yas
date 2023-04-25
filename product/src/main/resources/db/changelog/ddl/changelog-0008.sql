--liquibase formatted sql

--changeset dieunguyen:issue-467-1
alter table if exists product add column stock_quantity bigint default 0;

--changeset dieunguyen:issue-467-2
alter table if exists product add column tax_class_id bigserial;
