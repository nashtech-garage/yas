--liquibase formatted sql

--changeset vonhu:category1
insert into category (name, slug, display_order) values ('Laptop', 'laptop', 100)

--changeset vonhu:category2
insert into category (name, slug, display_order) values ('Iphone', 'iphone', 100)

--changeset vonhu:category3
insert into category (name, slug, display_order, parent_id) values ('Macbook', 'macbook', 100, 1)

--changeset khoahd7621:issue-528
update category set image_id = 5 where id = 1;
update category set name = 'Phone', image_id = 6 where id = 2;
update category set image_id = 7 where id = 3;

--changeset khoahd7621:issue-540
insert into category (name, slug, display_order, image_id) values ('Tablet', 'tablet', 100, 10);