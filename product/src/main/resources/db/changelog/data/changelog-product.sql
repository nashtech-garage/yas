--liquibase formatted sql

--changeset vonhu:product1
insert into product (is_active, is_allowed_to_order,is_published,is_featured,is_visible_individually, name, slug, price, short_description, brand_id,description)
values (true, true,true,true,true,'Dell XPS 15 9550','dell-xps-15-9550', 100000, 'CPU: 6th Gen Intel® Core™ i7 (Skylake) 6700HQ (Quad-Core, up to 3.2GHz)\nRAM: 8GB DDR4 2133MHz',1, 'The worlds smallest 15.6-inch laptop, the Dell XPS 15 stands apart with its stunning 4K UHD display and razor-thin profile. It is the only PC with a 15.6-inch')

--changeset vonhu:product2
insert into product (is_active, is_allowed_to_order,is_published,is_featured,is_visible_individually, name, slug, price, short_description, brand_id,description)
values (true, true,true,true,true,'iPad Pro Wi-Fi 4G 128GB','ipad-pro-wi-fi-4g-128gb-gold', 100000, 'Retina Display\nATX chip\niOS 9\nApps for iPad\nSlim and light design\nRAM: 8GB DDR4 2133MHz',2, 'The worlds smallest 15.6-inch laptop, the Dell XPS 15 stands apart with its stunning 4K UHD display and razor-thin profile. It is the only PC with a 15.6-inch')