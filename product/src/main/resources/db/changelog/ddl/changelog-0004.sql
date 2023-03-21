--liquibase formatted sql

--changeset nguyenvanhadncntt:issue-431-1
alter table if exists product add column remaining_quantity integer not null default 10;

--changeset nguyenvanhadncntt:issue-393-1
INSERT INTO product_image (image_id,product_id) VALUES (2,1), (4,2);
update product set thumbnail_media_id = 1 where id = 1;
update product set thumbnail_media_id = 3 where id = 2;

