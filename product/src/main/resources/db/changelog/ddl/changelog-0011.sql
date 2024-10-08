--liquibase formatted sql

--changeset nguyenvanhadncntt:issue-1126
alter table product add column weight decimal default 0;
alter table product add column length decimal default 0;
alter table product add column width decimal default 0;
alter table product add column height decimal default 0;
alter table product add column dimension_unit varchar(255);
