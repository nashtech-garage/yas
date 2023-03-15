--liquibase formatted sql

--changeset nguyenvanhadncntt:issue-389-1
alter table if exists brand add column is_published boolean;
alter table if exists category add column is_published boolean;