--liquibase formatted sql

--changeset khoahd7621:issue-586
insert into product_option_value (display_order, value, product_id, product_option_id) values
    (1, 'Gold', 2, 1),
    (1, 'Black', 2, 1),
    (1, '128GB', 2, 5),
    (1, '256GB', 2, 5),
    (1, '512GB', 2, 5);
