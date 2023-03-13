--liquibase formatted sql

--changeset lnghia:issue-352-1
alter table if exists product
    alter column description type text;

--changeset lnghia:issue-352-2
alter table if exists product
    alter column specification type text;
