--liquibase formatted sql

--changeset vonhu:product1
insert into product (is_active, is_allowed_to_order,is_published,is_featured,is_visible_individually, name, slug, price, short_description, brand_id,description)
values (true, true,true,true,true,'Dell XPS 15 9550','dell-xps-15-9550', 100000, 'CPU: 6th Gen Intel® Core™ i7 (Skylake) 6700HQ (Quad-Core, up to 3.2GHz)\nRAM: 8GB DDR4 2133MHz',1, 'The worlds smallest 15.6-inch laptop, the Dell XPS 15 stands apart with its stunning 4K UHD display and razor-thin profile. It is the only PC with a 15.6-inch')

--changeset vonhu:product2
insert into product (is_allowed_to_order,is_published,is_featured,is_visible_individually, name, slug, price, short_description, brand_id,description)
values (true, true,true,true,true,'iPad Pro Wi-Fi 4G 128GB','ipad-pro-wi-fi-4g-128gb-gold', 100000, 'Retina Display\nATX chip\niOS 9\nApps for iPad\nSlim and light design\nRAM: 8GB DDR4 2133MHz',2, 'The worlds smallest 15.6-inch laptop, the Dell XPS 15 stands apart with its stunning 4K UHD display and razor-thin profile. It is the only PC with a 15.6-inch')

--changeset nguyenvanhadncntt:issue-393-1
INSERT INTO product_image (image_id,product_id) VALUES (2,1), (4,2);
update product set thumbnail_media_id = 1 where id = 1;
update product set thumbnail_media_id = 3 where id = 2;

--changeset khoahd7621:issue-540
update product set thumbnail_media_id = 8 where id = 1;
update product set thumbnail_media_id = 9 where id = 2;

--changeset khoahd7621:issue-586
update product
set brand_id = 3,
    price = 16500000,
    specification = '<p>The worlds smallest 15.6-inch laptop, the Dell XPS 15 stands apart with its stunning 4K UHD display and razor-thin profile. It is the only PC with a 15.6-inch</p>'
where id = 1;
update product
set brand_id = 1,
    has_options = true,
    price = 30990000,
    thumbnail_media_id = 11,
    name = 'iPad Pro Wi-Fi 4G',
    slug = 'ipad-pro-wi-fi-4g',
    description = '<p>Ipad Pro Wi-Fi 4G with a breakthrough and modern design in addition to powerful configuration meets both entertainment and work needs. This <strong style="color: rgb(0, 0, 255);">Ipad</strong> promises to bring many great experiences to users.</p>',
    specification = '<h3 class="ql-align-justify"><strong>iPad Pro Wi-Fi 4G Review</strong></h3><p class="ql-align-justify"><br></p><p class="ql-align-justify">Ipad Pro Wi-Fi 4G with a breakthrough and modern design in addition to powerful configuration meets both entertainment and work needs. This iPad promises to bring many great experiences to users.</p><p class="ql-align-justify"><br></p><p class="ql-align-center"><a href="http://storefront/products/ipad-pro-wi-fi-4g" rel="noopener noreferrer" target="_blank" style="color: rgb(68, 68, 68);"><strong><img src="https://res.cloudinary.com/khoahd7621/image/upload/v1680681327/ipad-pro-11-wi-fi-4g-64gb-silver_ggws0b.png"></strong></a></p><h3 class="ql-align-justify"><strong>Bigger screen, more compact machine</strong></h3><p class="ql-align-justify"><br></p><p class="ql-align-justify">The new full-screen design makes the&nbsp;<strong style="color: rgb(0, 0, 255);">Ipad Pro Wi-Fi 4G&nbsp;</strong>still as compact as the previous 10.5-inch iPad. All screen bezels are thinner so that the screen takes up almost the entire front. You have a large screen, but the iPad is still very neat, providing plenty of space to create, work and play.</p><p class="ql-align-justify"><br></p><p class="ql-align-center"><a href="http://storefront/products/ipad-pro-wi-fi-4g" rel="noopener noreferrer" target="_blank" style="color: rgb(68, 68, 68);"><strong><img src="https://res.cloudinary.com/khoahd7621/image/upload/v1680681402/ipad-pro-11-wi-fi-4g-64gb-silver-1_slg2rw.png"></strong></a></p><h3 class="ql-align-justify"><br></h3><h3 class="ql-align-justify"><strong>Face ID feature</strong></h3><p class="ql-align-justify"><br></p><p class="ql-align-justify">The iPad Pro Wi-Fi 4G will recognize your face no matter what position the iPad is in. This is an important feature because users often have the habit of using iPad in landscape and portrait positions. Having Face ID and thin bezels also means Apple ditches the Home button on the iPad Pro in favor of gesture controls.</p><p class="ql-align-justify"><br></p><h3 class="ql-align-justify"><strong>Powerful configuration</strong></h3><p class="ql-align-justify"><br></p><p>The configuration on the Ipad Pro Wi-Fi 4G has been upgraded to make it more powerful in both application processing and graphics processing. Ipad Pro Wi-Fi 4G is equipped with A12X Bionic chip on 7nm technology, this chip has a total of 10 billion transistors with 8 cores (4 high-performance cores and 4 normal cores).</p><p class="ql-align-justify">On the A12X Bionic chip, there are also 7 GPU cores designed by Apple to bring very impressive graphics performance to the iPad Pro. Apple says in gaming, the new GPU brings the iPad Pro to new heights, the heights of Xbox One. If so, it would be terrible. Immediately after in a demo game, Apple showed that the graphics and rendering capabilities were extremely impressive, every detail of sweat, human face skin, hair was made very natural and sharp.</p><p class="ql-align-justify"><br></p><p class="ql-align-center"><a href="http://storefront/products/ipad-pro-wi-fi-4g" rel="noopener noreferrer" target="_blank" style="color: rgb(68, 68, 68);"><strong><img src="https://res.cloudinary.com/khoahd7621/image/upload/v1680681450/ipad-pro-11-wi-fi-4g-64gb-silver-2_mowh9g.png"></strong></a></p><h3 class="ql-align-justify"><br></h3><h3 class="ql-align-justify"><strong>Diverse connections - easy</strong></h3><p class="ql-align-justify"><br></p><p class="ql-align-justify">The iPad Pro Wi-Fi 4G is also the first iPad to switch from Lightning to USB-C. With the new connection port, Apple says the iPad Pro can output content to an external 5k display, connect to more accessories, and connect to music players. In addition, you can also charge your iPhone through this connector.</p><p class="ql-align-justify"><br></p><p class="ql-align-center"><a href="http://storefront/products/ipad-pro-wi-fi-4g" rel="noopener noreferrer" target="_blank" style="color: rgb(68, 68, 68);"><strong><img src="https://res.cloudinary.com/khoahd7621/image/upload/v1680681486/ipad-pro-11-wi-fi-4g-64gb-silver-3_nxdvni.png"></strong></a></p><h3 class="ql-align-justify"><br></h3><h3 class="ql-align-justify"><strong>All-day battery life</strong></h3><p class="ql-align-justify"><br></p><p class="ql-align-justify">The iPad Pro Wi-Fi 4G is not only thinner than today''s thinnest laptop, it also has a respectable battery life, making it the best mobile device for entertainment and work. iPad Pro battery life is up to 10 hours of continuous use, and when you''re not using it, it consumes almost no battery, although a connection is always guaranteed for all important notifications.</p>'
where id = 2;
insert into product (name,slug,price,parent_id) values
    ('iPad Pro Wi-Fi 4G 128GB Gold','ipad-pro-wi-fi-4g-128gb-gold',22000000,2),
    ('iPad Pro Wi-Fi 4G 256GB Gold','ipad-pro-wi-fi-4g-256gb-gold',25440000,2),
    ('iPad Pro Wi-Fi 4G 512GB Gold','ipad-pro-wi-fi-4g-512gb-gold',29330000,2),
    ('iPad Pro Wi-Fi 4G 128GB Black','ipad-pro-wi-fi-4g-128gb-black',21990000,2),
    ('iPad Pro Wi-Fi 4G 256GB Black','ipad-pro-wi-fi-4g-256gb-black',24990000,2),
    ('iPad Pro Wi-Fi 4G 512GB Black','ipad-pro-wi-fi-4g-512gb-black',28990000,2);
