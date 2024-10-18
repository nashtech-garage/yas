--changeset mochacr0:issue-1099-2
DROP TABLE IF EXISTS cart_item;
DROP TABLE IF EXISTS cart;

ALTER TABLE IF EXISTS cart_item_v2
RENAME TO cart_item;