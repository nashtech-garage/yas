--liquibase formatted sql

--changeset vonhu:product1
insert into product (is_active, is_allowed_to_order,is_published,is_featured,is_visible_individually, name, slug, price, short_description, brand_id,description)
values (true, true,true,true,true,'Dell XPS 15 9550','dell-xps-15-9550', 100000, 'CPU: 6th Gen Intel® Core™ i7 (Skylake) 6700HQ (Quad-Core, up to 3.2GHz)\nRAM: 8GB DDR4 2133MHz',1, 'The worlds smallest 15.6-inch laptop, the Dell XPS 15 stands apart with its stunning 4K UHD display and razor-thin profile. It is the only PC with a 15.6-inch')

--changeset vonhu:product2
insert into product (is_active, is_allowed_to_order,is_published,is_featured,is_visible_individually, name, slug, price, short_description, brand_id,description)
values (true, true,true,true,true,'iPad Pro Wi-Fi 4G 128GB','ipad-pro-wi-fi-4g-128gb-gold', 100000, 'Retina Display\nATX chip\niOS 9\nApps for iPad\nSlim and light design\nRAM: 8GB DDR4 2133MHz',2, 'The worlds smallest 15.6-inch laptop, the Dell XPS 15 stands apart with its stunning 4K UHD display and razor-thin profile. It is the only PC with a 15.6-inch')

--changeset nguyenvanhadncntt:issue-393-1
INSERT INTO product_image (image_id,product_id) VALUES (2,1), (4,2);
update product set thumbnail_media_id = 1 where id = 1;
update product set thumbnail_media_id = 3 where id = 2;

--changeset khoahd7621:issue-540
update product set thumbnail_media_id = 8 where id = 1;
update product set thumbnail_media_id = 9 where id = 2;

--changeset khoahd7621:issue-586
update product set brand_id = 3, has_options = true, price = 16500000 where id = 1;
update product set brand_id = 1, has_options = true, price = 30990000 where id = 2;
insert into product (name,slug,price,parent_id) values
    ('iPad Pro Wi-Fi 4G 128GB Gold','ipad-pro-wi-fi-4g-128gb-gold',22000000,2),
    ('iPad Pro Wi-Fi 4G 256GB Gold','ipad-pro-wi-fi-4g-256gb-gold',25440000,2),
    ('iPad Pro Wi-Fi 4G 512GB Gold','ipad-pro-wi-fi-4g-512gb-gold',29330000,2),
    ('iPad Pro Wi-Fi 4G 128GB Black','ipad-pro-wi-fi-4g-128gb-black',21990000,2),
    ('iPad Pro Wi-Fi 4G 256GB Black','ipad-pro-wi-fi-4g-256gb-black',24990000,2),
    ('iPad Pro Wi-Fi 4G 512GB Black','ipad-pro-wi-fi-4g-512gb-black',28990000,2);
