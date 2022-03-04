CREATE TABLE public.PRICE(
        id SERIAL PRIMARY KEY,
        product_id BIGINT not null,
        price NUMERIC not null,
        price_name varchar(255) not null,
        CONSTRAINT product_id_uni UNIQUE (product_id),
        CONSTRAINT price_name_uni UNIQUE (price_name)
);
