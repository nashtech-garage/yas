--liquibase formatted sql

--changeset vonhu:issue-promotion-0002
alter table if exists promotion add column is_active boolean;