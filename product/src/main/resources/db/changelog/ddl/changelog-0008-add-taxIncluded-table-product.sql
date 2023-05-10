--liquibase formatted sql

--changeset lnghia:issue-658
alter table if exists product add column tax_included boolean;