--liquibase formatted sql

--changeset nguyenvanhadncntt:issue-431-1
alter table if exists product add column remaining_quantity integer not null default 10;

--changeset toannguyenk:issue-470-1
alter table if exists product drop column remaining_quantity;
alter table if exists product add column stock_tracking_enabled boolean default TRUE;
