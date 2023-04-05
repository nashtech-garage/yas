--liquibase formatted sql

--changeset khoahd7621:issue-586
insert into product_option_combination (display_order, value, product_id, product_option_id) values
    (1, 'Gold', 3, 1),
    (1, 'Gold', 4, 1),
    (1, 'Gold', 5, 1),
    (1, 'Black', 6, 1),
    (1, 'Black', 7, 1),
    (1, 'Black', 8, 1),
    (1, '128GB', 3, 5),
    (1, '128GB', 6, 5),
    (1, '256GB', 4, 5),
    (1, '256GB', 7, 5),
    (1, '512GB', 5, 5),
    (1, '512GB', 8, 5);
