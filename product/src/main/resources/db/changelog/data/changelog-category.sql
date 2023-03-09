--liquibase formatted sql

--changeset vonhu:category1
insert into category (name, slug, display_order) values ('Laptop', 'laptop', 100)

--changeset vonhu:category2
insert into category (name, slug, display_order) values ('Iphone', 'iphone', 100)

--changeset vonhu:category3
insert into category (name, slug, display_order, parent_id) values ('Macbook', 'macbook', 100, 1)