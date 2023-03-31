--liquibase formatted sql

--changeset khoahd7621:issue-540
insert into product_category (product_id, category_id, display_order, is_featured_product) values
    (1, 1, 1, true),
    (2, 4, 1, true);