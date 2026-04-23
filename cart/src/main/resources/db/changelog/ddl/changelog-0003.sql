--changeset mochacr0:issue-1099-1
CREATE TABLE cart_item_v2 (
    customer_id VARCHAR(255) NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    created_by VARCHAR(255),
    created_on TIMESTAMP(6),
    last_modified_by VARCHAR(255),
    last_modified_on TIMESTAMP(6),
    CONSTRAINT pk_cart_item PRIMARY KEY (customer_id, product_id)
);
