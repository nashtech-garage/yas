--liquibase formatted sql

--changeset nguyenvanhadncntt:issue-483-1
create table inventory (id bigserial not null, product_id bigint not null, quantity integer not null);

