--liquibase formatted sql

--changeset nguyenvanhadncntt:issue-431-1
alter table if exists product add column remaining_quantity integer not null default 10;
