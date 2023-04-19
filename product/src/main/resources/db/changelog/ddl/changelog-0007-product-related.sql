--liquibase formatted sql

--changeset khoahd7621:issue-466
create table product_related (
    id bigserial not null,
    product_id bigint not null,
    related_product_id bigint not null,
    primary key (id),
    foreign key (product_id) references product(id),
    foreign key (related_product_id) references product(id));

