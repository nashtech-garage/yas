--liquibase formatted sql

--changeset dieunguyen:issue-467-1
alter table if exists product add column stock_quantity bigint default 0;
