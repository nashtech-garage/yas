DELETE FROM product_attribute_template;
DELETE FROM product_attribute_value;
DELETE FROM product_attribute;
DELETE FROM product_attribute_group;
DELETE FROM product_category;
DELETE FROM product_image;
DELETE FROM product_option_combination;
DELETE FROM product_option_value;
DELETE FROM product_option;
DELETE FROM product_related;
DELETE FROM product_template;
DELETE FROM product;
DELETE FROM brand;
DELETE FROM category;

INSERT INTO brand (id, "name", slug, is_published)
VALUES (1, 'Apple', 'apple', true);
INSERT INTO brand (id, "name", slug, is_published)
VALUES (2, 'Samsung', 'samsung', true);
INSERT INTO brand (id, "name", slug, is_published)
VALUES (3, 'Dell', 'dell', true);
INSERT INTO brand (id, "name", slug, is_published)
VALUES (4, 'LG', 'lg', true);
ALTER SEQUENCE brand_id_seq RESTART WITH 5;

INSERT INTO product_attribute_group (id, "name") VALUES(1, 'General');
INSERT INTO product_attribute_group (id, "name") VALUES(2, 'Screen');
INSERT INTO product_attribute_group (id, "name") VALUES(3, 'Connectivity');
INSERT INTO product_attribute_group (id, "name") VALUES(4, 'Camera');
INSERT INTO product_attribute_group (id, "name") VALUES(5, 'Memory');
ALTER SEQUENCE product_attribute_group_id_seq RESTART WITH 6;

INSERT INTO product_attribute
(id,"name",product_attribute_group_id) VALUES
(1,'CPU',1),
(2,'GPU',1),
(3,'OS',1),
(4,'Screen Size',2),
(5,'Display',2),
(8,'Main Camera',4),
(9,'Front-Facing Camera',4),
(10,'RAM',5),
(11,'Capacity',5),
(12,'Screen Resolution',2),
(13,'Power and Battery',1),
(14,'Connector',3),
(15,'Video Recording',4);
ALTER SEQUENCE product_attribute_id_seq RESTART WITH 16;

INSERT INTO product_template (id, "name") VALUES (1, 'Sample Template');
ALTER SEQUENCE product_template_id_seq RESTART WITH 2;

INSERT INTO product_attribute_template (id, product_attribute_id, product_template_id, display_order) VALUES(1, 1, 1, 0);
INSERT INTO product_attribute_template (id, product_attribute_id, product_template_id, display_order) VALUES(2, 2, 1, 0);
INSERT INTO product_attribute_template (id, product_attribute_id, product_template_id, display_order) VALUES(3, 10, 1, 0);
INSERT INTO product_attribute_template (id, product_attribute_id, product_template_id, display_order) VALUES(4, 11, 1, 0);
INSERT INTO product_attribute_template (id, product_attribute_id, product_template_id, display_order) VALUES(5, 4, 1, 0);
INSERT INTO product_attribute_template (id, product_attribute_id, product_template_id, display_order) VALUES(6, 3, 1, 0);
ALTER SEQUENCE product_attribute_template_id_seq RESTART WITH 7;

INSERT INTO product_option (id, "name") VALUES(1, 'Color');
INSERT INTO product_option (id, "name") VALUES(2, 'CPU');
INSERT INTO product_option (id, "name") VALUES(3, 'RAM');
INSERT INTO product_option (id, "name") VALUES(4, 'OS');
INSERT INTO product_option (id, "name") VALUES(5, 'Storage');
ALTER SEQUENCE product_option_id_seq RESTART WITH 6;

INSERT INTO product
(id,description,gtin,has_options,is_allowed_to_order,is_featured,is_published,is_visible_individually,meta_description,meta_keyword,meta_title,"name",price,short_description,sku,slug,specification,thumbnail_media_id,brand_id,stock_tracking_enabled,tax_included,stock_quantity,tax_class_id) VALUES
(1,'<p>iPhone 15 brings you Dynamic Island, a 48MP Main camera, and USB-C—all in a durable color-infused glass and aluminum design.</p>','IP15',false,true,true,true,true,'<h3><strong>iPhone 15 brings you Dynamic Island, a 48MP Main camera, and USB-C—all in a durable color-infused glass and aluminum design.</strong></h3>','','','iPhone 15',799.0,'<h3><strong>iPhone 15 brings you Dynamic Island, a 48MP Main camera, and USB-C—all in a durable color-infused glass and aluminum design.</strong></h3>','IP15','iphone-15','<p><br></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_01._CB577825247_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_02._CB577825247_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_03._CB577825247_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_04._CB577825247_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_05._CB577825247_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_06._CB577825247_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_07._CB577825247_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_08._CB577825247_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_09._CB577825247_.jpg" alt="image 1"></em></p>',7,1,false,true,10,1),
(2,'<p>iPhone 15 Pro. Forged in titanium and featuring the groundbreaking A17 Pro chip, a customizable Action button, and a more versatile Pro camera system.</p>','IP15PRO',false,true,true,true,true,'<h3><strong style="color: rgb(4, 12, 19);">iPhone 15 Pro. Forged in titanium and featuring the groundbreaking A17 Pro chip, a customizable Action button, and&nbsp;a more versatile Pro camera system.&nbsp;</strong></h3>','','','iPhone 15 Pro',899.0,'<h3><strong style="color: rgb(4, 12, 19);">iPhone 15 Pro. Forged in titanium and featuring the groundbreaking A17 Pro chip, a customizable Action button, and&nbsp;a more versatile Pro camera system.&nbsp;</strong></h3>','IP15PRO','iphone-15-pro','<p><br></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_Pro_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_01._CB577804077_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_Pro_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_02._CB577804077_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_Pro_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_03._CB577804077_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_Pro_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_04._CB577804077_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_Pro_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_05._CB577804077_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_Pro_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_06._CB577804077_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_Pro_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_07._CB577804077_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_Pro_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_08._CB577804077_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_Pro_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_09._CB577804077_.jpg" alt="image 1"></em></p>',12,1,false,true,10,1),
(3,'<p>iPhone 15 Plus brings you Dynamic Island, a 48MP Main camera, and USB-C—all in a durable color-infused glass and aluminum design.</p>','IP15PLUS',false,true,true,true,true,'<h3><strong style="color: rgb(4, 12, 19);">iPhone 15 Plus brings you Dynamic Island, a 48MP Main camera, and USB-C—all in a durable color-infused glass and aluminum design.</strong></h3>','','','iPhone 15 Plus',859.0,'<h3><strong style="color: rgb(4, 12, 19);">iPhone 15 Plus brings you Dynamic Island, a 48MP Main camera, and USB-C—all in a durable color-infused glass and aluminum design.</strong></h3>','IP15PLUS','iphone-15-plus','<p><br></p><p><br></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_01._CB577825247_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_02._CB577825247_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_03._CB577825247_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_04._CB577825247_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_05._CB577825247_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_06._CB577825247_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_07._CB577825247_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_08._CB577825247_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_09._CB577825247_.jpg" alt="image 1"></em></p>',17,1,false,true,10,1),
(4,'<p>iPhone 15 Pro Max. Forged in titanium and featuring the groundbreaking A17 Pro chip, a customizable Action button, and the most powerful iPhone camera system ever.</p>','IP15PROMAX',false,true,true,true,true,'<h3><strong style="color: rgb(4, 12, 19);">iPhone 15 Pro Max. Forged in titanium and featuring the groundbreaking A17 Pro chip, a customizable Action button, and the most powerful iPhone camera system ever.&nbsp;</strong></h3>','','','iPhone 15 Pro Max',1199.0,'<h3><strong style="color: rgb(4, 12, 19);">iPhone 15 Pro Max. Forged in titanium and featuring the groundbreaking A17 Pro chip, a customizable Action button, and the most powerful iPhone camera system ever.&nbsp;</strong></h3>','IP15PROMAX','iphone-15-pro-max','<p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_Pro_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_01._CB577804077_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_Pro_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_02._CB577804077_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_Pro_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_03._CB577804077_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_Pro_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_04._CB577804077_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_Pro_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_05._CB577804077_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_Pro_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_06._CB577804077_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_Pro_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_07._CB577804077_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_Pro_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_08._CB577804077_.jpg" alt="image 1"></em></p><p class="ql-align-center"><em><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPhone_15_Pro_5G_Q423_Web_Marketing_Page_PSD_Bronze_L_09._CB577804077_.jpg" alt="image 1"></em></p>',22,1,false,true,10,1),
(5,'<p>Unlock non-stop creativity with the new Dell XPS 14, a perfect balance of go-anywhere mobility and high performance.​</p>','DELL-9440',false,true,true,true,true,'<h2><strong style="color: rgb(4, 12, 19);">Unlock non-stop creativity with the new Dell XPS 14, a perfect balance of go-anywhere mobility and high performance.​&nbsp;</strong></h2>','','','Laptop Dell XPS 14 9440',2299.0,'<h2><strong style="color: rgb(4, 12, 19);">Unlock non-stop creativity with the new Dell XPS 14, a perfect balance of go-anywhere mobility and high performance.​&nbsp;</strong></h2>','DELL-9440','laptop-dell-xps-14-9440','<ul><li><span style="color: rgb(15, 17, 17);"><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/074d6af3-eb5e-4388-b02f-036f70072e72.__CR0,0,1464,600_PT0_SX1464_V1___.jpg" alt="B0CZY2L3JY- XPS 9440 Laptop"></span></li></ul><h1><br></h1><h1>The XPS 14</h1><p><br></p><p>Unlock non-stop creativity with the XPS 14 laptop, a perfect balance of go-anywhere mobility and high performance.</p><ul><li>14.5" FHD+ (1920 x 1200) InfinityEdge non-touch, 500 nit Display</li><li>Intel Core Ultra 7 155H (24MB Cache, 16 cores, up to 4.8 GHz) Processor</li><li>Intel Arc Graphics</li><li>512GB M.2 PCIe NVMe Solid State Drive</li><li>16GB LPDDR5x Dual Channel at 6400MT/s Memory</li></ul><p><br></p><h1>Windows 11 brings you closer to what you love</h1><p><br></p><p>Family, friends, obsessions, music, creations - Windows 11 is the one place for it all. With a fresh new feel and tools that make it easier to be efficient, it has what you need for whatever’s next.</p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/4f81e358-0da2-4274-be2a-463476746afd.__CR0,0,800,600_PT0_SX800_V1___.jpg" alt="windows-11"></p><h1>Sustainability Comes Naturally.</h1><p><br></p><p>Dell incorporates sustainability into everything we do, from choosing sustainable materials for products and packaging to reusing, remanufacturing or responsibly recycling them at the end of their lifecycle.</p><p><br></p><h1>Iconic design</h1><p><br></p><p>Crafted with machined (CNC) aluminium and Gorilla Glass 3 to deliver a strong, lightweight laptop. The XPS 14 is available in Platinum for an elegant, minimalistic design.</p><h1 class="ql-align-center"><br></h1><p><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/99254aed-6e11-492a-a546-e01a87f36fab.__CR0,0,1464,600_PT0_SX1464_V1___.jpg" alt="B0CZY2L3JY- XPS 9440 Laptop"></p><p class="ql-align-center">The XPS 14 perfectly balances performance and mobility in a stunning form factor. Create on the go with its lightweight 1.68 kg design, slim 18 mm size and long battery life.</p><p class="ql-align-center"><em>Weights vary depending on configurations and manufacturing variability.﻿</em></p><h1><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/f6399faa-c4b3-4f54-8f91-79fc30235cfb.__CR0,0,800,600_PT0_SX800_V1___.jpg" alt="laptop-xps-9340-image-6-800x600-1304041735-.jpg"></h1><p><br></p><p>GPU</p><p>High Throughput: Ideal for AI-accelerated digital content creation and video editing with creation apps like Adobe.</p><p><strong>NPU</strong></p><p>Low Power: Ideal for running AI features like Microsoft Studio Effects during video calls.</p><p><strong>CPU</strong></p><p>Fast Response: Ideal for real time multi-tasking and AI workload such as noise cancellation and instant transcription.</p><p><span style="color: var(--app-black); background-color: var(--bs-body-bg);"><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/564900fe-a86c-4b60-a595-f5171828982d.__CR0,0,800,600_PT0_SX800_V1___.jpg" alt="XPS9440"></span></p><p><br></p><p class="ql-align-center"><br></p>',27,3,false,true,10,1),
(6,'<p>WHY IPAD PRO — iPad Pro is the ultimate iPad experience in an impossibly thin and light design. Featuring the breakthrough Ultra Retina XDR display, outrageous performance from the M4 chip, superfast wireless connectivity,* and compatibility with Apple Pencil Pro.* Plus powerful productivity features in iPadOS.</p>','IPAD-PRO-11',false,true,true,true,true,'<h3><strong style="color: rgb(4, 12, 19);">The new iPad Pro is impossibly thin, featuring outrageous performance with the Apple M4 chip, a breakthrough Ultra Retina XDR display, and superfast Wi-Fi 6E. </strong></h3>','','','iPad Pro 11 M4',949.0,'<h3><strong style="color: rgb(4, 12, 19);">The new iPad Pro is impossibly thin, featuring outrageous performance with the Apple M4 chip, a breakthrough Ultra Retina XDR display, and superfast Wi-Fi 6E. </strong></h3>','IPAD-PRO-11','ipad-pro-11-m4','<p><br></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPad_Pro_M4_5G_Q324_Web_Marketing_Page_PSD_Bronze_L_01._CB558435783_.jpg" alt="iphone11"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPad_Pro_M4_5G_Q324_Web_Marketing_Page_PSD_Bronze_L_02._CB558435783_.jpg" alt="iphone11"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPad_Pro_M4_5G_Q324_Web_Marketing_Page_PSD_Bronze_L_03._CB558435783_.jpg" alt="iphone11"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPad_Pro_M4_5G_Q324_Web_Marketing_Page_PSD_Bronze_L_04._CB558435783_.jpg" alt="iphone11"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPad_Pro_M4_5G_Q324_Web_Marketing_Page_PSD_Bronze_L_05._CB558435783_.jpg" alt="iphone11"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPad_Pro_M4_5G_Q324_Web_Marketing_Page_PSD_Bronze_L_06._CB558435783_.jpg" alt="iphone11"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPad_Pro_M4_5G_Q324_Web_Marketing_Page_PSD_Bronze_L_07._CB558435783_.jpg" alt="iphone11"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPad_Pro_M4_5G_Q324_Web_Marketing_Page_PSD_Bronze_L_08._CB558435783_.jpg" alt="iphone11"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_iPad_Pro_M4_5G_Q324_Web_Marketing_Page_PSD_Bronze_L_09._CB558435783_.jpg" alt="iphone11"></p>',30,1,false,true,10,1),
(7,'<p><span style="color: rgb(4, 12, 19);">Meet Galaxy Tab S9 FE+, the powerful tablet that’s made just for you. A universe of possibilities awaits with Tab S9 FE+.&nbsp;</span></p>','samsung-galaxy-tab-s9',false,true,true,true,true,'','','','Samsung - Galaxy Tab S9',559.0,'Galaxy AI is here. Search like never before¹, let transcript assist² take the notes for you, format your notes into a clear summary,³ and effortlessly edit your photos⁴ -all from your tablet, all with AI.','samsung-galaxy-tab-s9','samsung-galaxy-tab-s9','<h2><strong>From the manufacturer</strong></h2><p><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/f8c45598-eef5-4bea-8b9b-e0ded99277cf.__CR0,0,1464,600_PT0_SX1464_V1___.jpg" alt="samsung galaxy, Tab FE"></p><p><br></p><h1><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/5f915b97-b66c-4633-bc5c-4afd1be7fad7.__CR0,0,800,600_PT0_SX800_V1___.jpg" alt="samsung galaxy, Tab FE"></h1><h2><strong>Power up your notetaking with S Pen</strong></h2><p>Write, sketch, draw on your Samsung tablet with your favourite notetaking app and the redesigned water and dust-resistant inbox S Pen.</p><p>Now in four trendy colours, it attaches magnetically to the back of your device and matches its style. Galaxy Tab S9 FE and Tab S9 FE+ even support S Pen Creator Edition to unleash your full creative potential.</p><p><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/193238de-4b0a-4f33-9534-4287862ffd5c.__CR0,0,800,600_PT0_SX800_V1___.jpg" alt="samsung galaxy, Tab FE"><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/32981c0d-7b7d-41e7-94ca-eb090b46f4b4.__CR0,0,800,600_PT0_SX800_V1___.jpg" alt="samsung galaxy, Tab FE"><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/10a68d2a-0105-4236-a39b-0b91ce6fd581.__CR0,0,800,600_PT0_SX800_V1___.jpg" alt="samsung galaxy, Tab FE"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/4c7a718d-2288-4e79-8632-161ff9ef0e6b.__CR0,0,1464,600_PT0_SX1464_V1___.jpg" alt="samsung galaxy, Tab FE"></p><p class="ql-align-center">When the day gets tough, Tab and S Pen stay sturdy and durable. The smooth tablet design is suited up with a strong full-metal frame and the IP68 rating means it''s our first water and dust-resistant Galaxy S FE tablet with S Pen ever.</p><p class="ql-align-center"><br></p><p class="ql-align-center">Equipped with our high-performing Exynos 1380 chipset, Galaxy Tab S9 FE and Tab S9 FE+ keep multitasking, streaming and gaming smooth and fast. Choose up to 256GB internal storage, then grab your microSD to add on more storage in case you need even more room for your videos, compositions, hi-res photos and more.<img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/dba82c24-d7d0-4ac0-99bf-0260fb91251b.__CR0,0,800,600_PT0_SX800_V1___.jpg" alt="samsung galaxy, Tab FE"><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/a88dc2da-6eaf-419d-80af-e0f281868e1e.__CR0,0,800,600_PT0_SX800_V1___.jpg" alt="samsung galaxy, Tab FE"></p><p><br></p><p class="ql-align-center"><br></p>',37,2,false,true,10,1),
(8,'<p><span style="color: rgb(15, 17, 17);">Galaxy AI: Experience the future of mobile technology with the Galaxy Z Fold6, put PC-Like Power in your pocket, Galaxy Z Fold6 now Super Charged with Galaxy AI. Thinner &amp; lighter with a pocket-worthy silhouette &amp; an even brighter, awe inspiring fold out screen</span></p>','samsung-galaxy-z-fold6',false,true,true,true,true,'<p><br></p>','','','Samsung Galaxy Z Fold6',2099.0,'Galaxy AI is here. Search like never before¹, let transcript assist² take the notes for you, format your notes into a clear summary,³ and effortlessly edit your photos⁴ -all from your tablet, all with AI.','samsung-galaxy-z-fold6','samsung-galaxy-z-fold6','<h1 class="ql-align-center"><strong>Galaxy AI is here</strong></h1><p><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/627c7301-a2d5-4f57-af53-32d6ee855015.__CR0,0,1464,600_PT0_SX1464_V1___.jpg" alt="Fold6"></p><p class="ql-align-center">Put PC-like power in your pocket, Galaxy Z Fold6. More powerful than ever with its super-slim, productive screen.</p><h1 class="ql-align-center"><strong>The lightest and the thinnest </strong></h1><p><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/b236644e-09d9-4476-a9f3-f183e6b5253d.__CR0,0,1464,600_PT0_SX1464_V1___.jpg" alt="design"></p><h3><br></h3><h1><strong>Our most immersive smartphone gaming.</strong></h1><p><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/a7e6d717-cfed-40a4-8af2-4440b1624b9a.__CR0,0,800,600_PT0_SX800_V1___.jpg" alt="display"></p><p class="ql-align-center"><br></p><h1><strong>Powerful gaming with the fastest processor on a Samsung foldable</strong></h1><p><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/357d98ec-77dd-4cc8-bc4c-d7e7340665d6.__CR0,0,800,600_PT0_SX800_V1___.jpg" alt="PROCESSOR"></p><p class="ql-align-center"><br></p><p class="ql-align-center"><br></p><p class="ql-align-center"><br></p><h1 class="ql-align-center"><strong>What''s in the box?</strong></h1><p><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/aa518f7d-5c7c-4a4a-a6f0-f7f6d7459f75.__CR0,0,1464,600_PT0_SX1464_V1___.jpg" alt="in the box"></p>',41,2,false,true,10,1),
(12,'<p>The 14-inch MacBook Pro blasts forward with M3 Pro and M3 Max, radically advanced chips that drive even greater performance for more demanding workflows.</p>','macbook-pro-14-m3-pro',false,true,true,true,true,'<p><span style="color: rgb(4, 12, 19);">The 14-inch MacBook Pro blasts forward with M3 Pro and M3 Max, radically advanced chips that drive even greater performance for more demanding workflows.</span></p>','','','MacBook Pro 14 M3 Pro',1699.0,'<p><span style="color: rgb(4, 12, 19);">The 14-inch MacBook Pro blasts forward with M3 Pro and M3 Max, radically advanced chips that drive even greater performance for more demanding workflows.</span></p>','macbook-pro-14-m3-pro','macbook-pro-14-m3-pro','<p><br></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Pro_M3_Q124_Web_Marketing_Page_PSD_Bronze_L_01._CB573742371_.jpg" alt="iphone11"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Pro_M3_Q124_Web_Marketing_Page_PSD_Bronze_L_02._CB573742371_.jpg" alt="iphone11"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Pro_M3_Q124_Web_Marketing_Page_PSD_Bronze_L_03._CB573742371_.jpg" alt="iphone11"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Pro_M3_Q124_Web_Marketing_Page_PSD_Bronze_L_04._CB573742371_.jpg" alt="iphone11"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Pro_M3_Q124_Web_Marketing_Page_PSD_Bronze_L_05._CB573742371_.jpg" alt="iphone11"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Pro_M3_Q124_Web_Marketing_Page_PSD_Bronze_L_06._CB573742371_.jpg" alt="iphone11"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Pro_M3_Q124_Web_Marketing_Page_PSD_Bronze_L_07._CB573742371_.jpg" alt="iphone11"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Pro_M3_Q124_Web_Marketing_Page_PSD_Bronze_L_08._CB573742371_.jpg" alt="iphone11"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Pro_M3_Q124_Web_Marketing_Page_PSD_Bronze_L_09._CB573742371_.jpg" alt="iphone11"></p>',54,1,false,true,10,1),
(13,'<p>Supercharged by the next-generation M2 chip, the redesigned MacBook Air</p>','macbook-air-m2',false,true,true,true,true,'<h3><span style="color: rgb(4, 12, 19);">Supercharged by the next-generation M2 chip, the redesigned MacBook Air</span></h3>','','','MacBook Air M2',729.0,'<h3><span style="color: rgb(4, 12, 19);">Supercharged by the next-generation M2 chip, the redesigned MacBook Air</span></h3>','macbook-air-m2','macbook-air-m2','<p><br></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Air_M2_Q323_Web_Marketing_Page_PSD_Bronze_L_01._CB588073343_.jpg" alt="image 1"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Air_M2_Q323_Web_Marketing_Page_PSD_Bronze_L_02._CB588073343_.jpg" alt="image 1"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Air_M2_Q323_Web_Marketing_Page_PSD_Bronze_L_03._CB588073343_.jpg" alt="image 1"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Air_M2_Q323_Web_Marketing_Page_PSD_Bronze_L_04._CB588073343_.jpg" alt="image 1"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Air_M2_Q323_Web_Marketing_Page_PSD_Bronze_L_05._CB588073343_.jpg" alt="image 1"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Air_M2_Q323_Web_Marketing_Page_PSD_Bronze_L_06._CB588073343_.jpg" alt="image 1"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Air_M2_Q323_Web_Marketing_Page_PSD_Bronze_L_07._CB588073343_.jpg" alt="image 1"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Air_M2_Q323_Web_Marketing_Page_PSD_Bronze_L_08._CB588073343_.jpg" alt="image 1"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Air_M2_Q323_Web_Marketing_Page_PSD_Bronze_L_09._CB588073343_.jpg" alt="image 1"></p>',55,1,false,true,10,1),
(14,'<p>The M3 chip brings even greater capabilities to the superportable 15-inch MacBook Air</p>','macbook-air-m3',false,true,true,true,true,'<h2><strong style="color: rgb(4, 12, 19);">The M3 chip brings even greater capabilities to the superportable 15-inch MacBook Air</strong></h2>','','','MacBook Air M3',1349.0,'<h2><strong style="color: rgb(4, 12, 19);">The M3 chip brings even greater capabilities to the superportable 15-inch MacBook Air</strong></h2>','macbook-air-m3','macbook-air-m3','<p><br></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Air_M3_Q224_Web_Marketing_Page_PSD_Bronze_L_01._CB580541568_.jpg" alt="image 1"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Air_M3_Q224_Web_Marketing_Page_PSD_Bronze_L_02._CB580541568_.jpg" alt="image 1"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Air_M3_Q224_Web_Marketing_Page_PSD_Bronze_L_03._CB580541568_.jpg" alt="image 1"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Air_M3_Q224_Web_Marketing_Page_PSD_Bronze_L_04._CB580541568_.jpg" alt="image 1"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Air_M3_Q224_Web_Marketing_Page_PSD_Bronze_L_05._CB580541568_.jpg" alt="image 1"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Air_M3_Q224_Web_Marketing_Page_PSD_Bronze_L_06._CB580541568_.jpg" alt="image 1"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Air_M3_Q224_Web_Marketing_Page_PSD_Bronze_L_07._CB580541568_.jpg" alt="image 1"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Air_M3_Q224_Web_Marketing_Page_PSD_Bronze_L_08._CB580541568_.jpg" alt="image 1"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/APlus/GBEN_MacBook_Air_M3_Q224_Web_Marketing_Page_PSD_Bronze_L_09._CB580541568_.jpg" alt="image 1"></p>',59,1,false,true,10,1),
(15,'<p>Unlock your creativity with the tiny but mighty Galaxy Z Flip6, the Galaxy AI powered pocket perfect foldable built for those who want to make a big statement</p>','Samsung Galaxy Z Flip6 ',false,true,true,true,true,'<p><span style="color: rgb(4, 12, 19);">Unlock your creativity with the tiny but mighty Galaxy Z Flip6, the Galaxy AI powered pocket perfect foldable built for those who want to make a big statement</span></p>','','','Samsung Galaxy Z Flip6 ',1099.0,'<p><span style="color: rgb(4, 12, 19);">Unlock your creativity with the tiny but mighty Galaxy Z Flip6, the Galaxy AI powered pocket perfect foldable built for those who want to make a big statement</span></p>','Samsung Galaxy Z Flip6 ','samsung-galaxy-z-flip6','<p class="ql-align-center">Galaxy AI is here</p><p><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/8d5930e7-0dbd-4324-8c03-f1f1e0e6ee7c.__CR0,0,1464,600_PT0_SX1464_V1___.jpg" alt="Galaxy Z Flip6"></p><p class="ql-align-center"><em>Your self-expression tool, Galaxy Z Flip6, is more compact and eye-catching, with Galaxy AI and a pro-level 50MP camera that''s photoshoot-ready.³</em></p><p><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/5b022f4e-703c-4493-a2d8-c206c4be0b17.__CR0,0,1464,600_PT0_SX1464_V1___.jpg" alt="Flip6"></p><p class="ql-align-center"><em>The most compact, big impact</em></p><p class="ql-align-center"><br></p><p>FlexCam</p><p><strong><em>FlexCam uses Auto Zoom to zoom in or out on subjects, and provides a preview on the FlexWindow for a hands-free selfie experience</em></strong></p><p class="ql-align-center"><span style="color: rgb(15, 17, 17);"><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/27c52bb3-cd3e-4e69-bee3-b3ee44666e02.__CR0,0,800,600_PT0_SX800_V1___.jpg" alt="Flex Cam"></span></p><p>Portraits</p><p><strong><em>50MP Camera. Stunning Portraits.</em></strong></p><p><br></p><p class="ql-align-center">Capture stunning shots during the day but also at night, and even in dimly lit areas, thanks to ProVisual Engine.</p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/39253119-163b-4c93-9836-52cfaef07642.__CR0,0,800,600_PT0_SX800_V1___.jpg" alt="Authentic Portrait"></p><p>Camera System</p><p><strong><em>The most powerful camera on a Galaxy Z Flip, now with AI</em></strong></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/d9af8f98-4f50-479d-895a-faf92a3978f8.__CR0,0,800,600_PT0_SX800_V1___.jpg" alt="Camera Spec"></p><p>Battery &amp; Vapour Chamber</p><p><strong><em>Galaxy Z Flip''s longest lasting battery and first ever Vapour Chamber</em></strong></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/f483436b-6368-4dba-bc97-ebe848b888d0.__CR0,0,800,600_PT0_SX800_V1___.jpg" alt="Vapour Chamber Battery"></p><p class="ql-align-center"><br></p><p class="ql-align-center"><span style="color: rgb(15, 17, 17);"><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/38418cb2-e2df-4fbf-8947-bd7b072e92ec.__CR0,0,1464,600_PT0_SX1464_V1___.jpg" alt="Flip6 What''s in the box"></span></p>',64,2,false,true,10,1),
(16,'<p>Intel Core Ultra processors deliver the next level in an immersive graphics experience, and high-performance low power processing</p>','dell-inspiron-16-5640-laptop-16',false,true,true,true,true,'<p><span style="color: rgb(4, 12, 19);">Intel&nbsp;Core Ultra processors&nbsp;deliver the next level in an immersive graphics experience, and high-performance low power processing</span></p>','','','Dell Inspiron 16 5640 Laptop 16"',584.0,'<p><span style="color: rgb(4, 12, 19);">Intel&nbsp;Core Ultra processors&nbsp;deliver the next level in an immersive graphics experience, and high-performance low power processing</span></p>','dell-inspiron-16-5640-laptop-16','dell-inspiron-16-5640-laptop-16','<p><span style="color: rgb(15, 17, 17);"><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/1bdb38f3-dcbe-49c0-bcac-0a8a523fb7b3.__CR0,0,1464,600_PT0_SX1464_V1___.jpg" alt="laptop-inspiron-5640-banner-1464x600-b0cz76sqn4.jpg"></span></p><p><br></p><h1>Immersive screen, unlimited productivity.</h1><p><br></p><p>Enjoy seamless productivity and immersive entertainment with Intel Core processors and Intel Graphics on a large screen.</p><ul><li>16.0-inch 16:10 FHD+ (1920 x 1200) Anti-Glare Non-Touch 250nits WVA Display with ComfortView Support</li><li>Intel Core 7 processor 150U (12MB cache, 10 cores, 12 threads, 5.4 GHz)</li><li>Intel Graphics</li><li>1TB M.2 PCIe NVMe Solid State Drive</li><li>16GB, 2x8GB, DDR5, 5200 MT/s Memory</li></ul><p><br></p><p><br></p><h1>Windows 11 brings you closer to what you love</h1><p><br></p><p>Family, friends, obsessions, music, creations Windows 11 is the one place for it all. With a fresh new feel and tools that make it easier to be efficient, it has what you need for whatever’s next.</p><p><br></p><p><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/4f81e358-0da2-4274-be2a-463476746afd.__CR0,0,800,600_PT0_SX800_V1___.jpg" alt="windows-11"></p><p><br></p><h1>Sustainability Comes Naturally.</h1><p><br></p><p>Dell aims to eliminate waste by continually reusing resources in our technology.</p><p><br></p><p><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/7e3256ee-0bd5-47a5-983e-a03cc1c49330.__CR0,0,1464,600_PT0_SX1464_V1___.jpg" alt="laptop-inspiron-5640-image-1-1464x600-b0cz76sqn4-lifestyle-image.jpg"></p><p class="ql-align-center">Enjoy seamless productivity and immersive entertainment with Intel Core Processors and Intel Graphics on a large immersive screen.</p><p><br></p><p><br></p><h1 class="ql-align-center">Ports and Slots</h1><p><img src="https://m.media-amazon.com/images/S/aplus-media-library-service-media/93217aad-5267-4349-bf92-cb988bb82cf5.__CR0,0,1464,600_PT0_SX1464_V1___.jpg" alt="laptop-inspiron-5640-image-2-1464x600-b0cz76sqn4-port-&amp;amp;-slot.jpg"></p><p class="ql-align-center"><em>1. SD card reader | 2. Universal Audio Jack | 3. USB 3.2 Gen 1 Type-A | 4. Lock slot |5. Power jack | 6. HDMI 1.4 | 7. USB 3.2 Gen 1 Type-A | 8. USB 3.2 Gen 2 Type-C (DP/PowerDelivery)</em></p>',71,3,false,true,10,1),
(17,'<p>Colorfully reimagined and more versatile than ever. With an all-screen design,10.9-inch Liquid Retina display</p>','ipad-109-inch-10th-generation',false,true,true,true,true,'<p><span style="color: rgb(4, 12, 19);">Colorfully reimagined and more versatile than ever. With an all-screen design,10.9-inch Liquid Retina display</span></p>','','','iPad 10.9-Inch (10th Generation)',349.0,'<p><span style="color: rgb(4, 12, 19);">Colorfully reimagined and more versatile than ever. With an all-screen design,10.9-inch Liquid Retina display</span></p>','ipad-109-inch-10th-generation','ipad-109-inch-10th-generation','<p><br></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/Apple/GBEN_iPad_10th_Gen_5G_Q124_Web_Marketing_Page_PSD_Bronze_L_01._CB573905506_.jpg" alt="iphone11"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/img15/MarchEye/Premiumaplus/GBEN_iPad_10th_Gen_5G_Q124_Web_Marketing_Page_PSD_Bronze_L_02._CB574385677_.jpg" alt="iphone11"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/img15/MarchEye/Premiumaplus/GBEN_iPad_10th_Gen_5G_Q124_Web_Marketing_Page_PSD_Bronze_L_03._CB574385677_.jpg" alt="iphone11"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/img15/MarchEye/Premiumaplus/GBEN_iPad_10th_Gen_5G_Q124_Web_Marketing_Page_PSD_Bronze_L_04._CB574385677_.jpg" alt="iphone11"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/img15/MarchEye/Premiumaplus/GBEN_iPad_10th_Gen_5G_Q124_Web_Marketing_Page_PSD_Bronze_L_05._CB574385677_.jpg" alt="iphone11"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/img15/MarchEye/Premiumaplus/GBEN_iPad_10th_Gen_5G_Q124_Web_Marketing_Page_PSD_Bronze_L_06._CB574385677_.jpg" alt="iphone11"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/img15/MarchEye/Premiumaplus/GBEN_iPad_10th_Gen_5G_Q124_Web_Marketing_Page_PSD_Bronze_L_07._CB574385677_.jpg" alt="iphone11"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/img15/MarchEye/Premiumaplus/GBEN_iPad_10th_Gen_5G_Q124_Web_Marketing_Page_PSD_Bronze_L_08._CB574385677_.jpg" alt="iphone11"></p><p class="ql-align-center"><img src="https://m.media-amazon.com/images/G/02/img15/MarchEye/Premiumaplus/GBEN_iPad_10th_Gen_5G_Q124_Web_Marketing_Page_PSD_Bronze_L_09._CB574385677_.jpg" alt="iphone11"></p><p><br></p>',76,1,false,true,10,1);
ALTER SEQUENCE product_id_seq RESTART WITH 18;

ALTER SEQUENCE product_attribute_value_id_seq RESTART WITH 1;
INSERT INTO product_attribute_value
    (value,product_id,product_attribute_id) VALUES
    ('M3 Pro chip',12,1),
    ('14-core GPU',12,2),
    ('Apple M3',14,1),
    ('Apple M3 10-core',14,2),
    ('8 GB',14,10),
    ('512GB',14,11),
    ('15.3 inches',14,4),
    ('macOS Sonoma',14,3),
    ('Snapdragon® 8 Gen 3 for Galaxy 3.4 gigahertz',15,1),
    ('Snapdragon® 8 GPU',15,2),
    (' 12 GB',15,10),
    ('256GB',15,11),
    ('6.7 inches',15,4),
    ('Android 14',15,3),
    ('2640 x 1080',15,12),
    ('Intel Core Ultra 7 Series 1',16,1),
    ('Intel Arc',16,2),
    ('16 gigabytes',16,10),
    ('1000 gigabytes',16,11),
    ('16 inches',16,4);
INSERT INTO product_attribute_value
    (value,product_id,product_attribute_id) VALUES
    ('Win 11 Pro',16,3),
    ('1920 x 1200 (Full HD+)',16,12),
    ('A14 Bionic chip',17,1),
    ('A14 Bionic chip',17,2),
    ('4GB',17,10),
    ('64 gigabytes',17,11),
    ('10.9 inches',17,4),
    ('Apple iPadOS',17,3),
    ('2360 x 1640',17,12),
    ('Liquid Retina display',17,5),
    ('Apple A16 Bionic (4 nm) Hexa-core (2x3.46 GHz Everest + 4x2.02 GHz Sawtooth)',1,1),
    ('Apple A16 Bionic (4 nm) ',1,2),
    ('Super Retina XDR OLED, HDR10, Dolby Vision, 1000 nits (HBM), 2000 nits (peak)',1,5),
    ('48MP Main: 26 mm, ƒ/1.6 aperture, sensor‑shift optical image stabilization, 100% Focus Pixels, support for super-high-resolution photos (24MP and 48MP)',1,8),
    ('12MP TrueDepth camera',1,9),
    ('USB‑C',1,14),
    ('128GB, 256GB, 512GB',1,11),
    ('4K@24/25/30/60fps, 1080p@25/30/60/120/240fps, HDR, Dolby Vision HDR (up to 60fps), stereo sound rec.',1,15),
    ('Li-Ion 3349 mAh, non-removable - Wired, PD2.0, 50% in 30 min (advertised)',1,13),
    ('Apple A17 Pro (3 nm) Hexa-core (2x3.78 GHz + 4x2.11 GHz)',2,1);
INSERT INTO product_attribute_value
    (value,product_id,product_attribute_id) VALUES
    ('Apple GPU (6-core graphics)',2,2),
    ('48 MP, f/1.8, 24mm (wide), 1/1.28", 1.22µm, dual pixel PDAF, sensor-shift OIS TOF 3D LiDAR scanner (depth)',2,8),
    ('12 MP, f/1.9, 23mm (wide), 1/3.6", PDAF, OIS SL 3D, (depth/biometrics sensor)',2,9),
    ('LTPO Super Retina XDR OLED, 120Hz, HDR10, Dolby Vision, 1000 nits (typ), 2000 nits (HBM) Always-On display',2,5),
    ('Li-Ion 3274 mAh, non-removable Wired, PD2.0, 50% in 30 min (advertised)',2,13),
    ('4K@24/25/30/60fps, 1080p@25/30/60/120fps, gyro-EIS',2,15),
    ('USB Type-C 3.2 Gen 2, DisplayPort',2,14),
    ('6.1 inches, 91.3 cm2 (~88.2% screen-to-body ratio)',2,4),
    ('Apple A16 Bionic (4 nm) Hexa-core (2x3.46 GHz Everest + 4x2.02 GHz Sawtooth)',3,1),
    ('Apple GPU (5-core graphics)',3,2),
    ('48 MP, f/1.6, 26mm (wide), 1/1.56", 1.0µm, dual pixel PDAF, sensor-shift OIS 12 MP, f/2.4, 13mm, 120˚ (ultrawide), 0.7µm',3,8),
    ('6.3" AMOLED - 153.5mm x 68.1mm x 12.1mm + 7.6" AMOLED - 153.5mm x 132.6mm x 5.6mm',3,9),
    ('USB Type-C 2.0, DisplayPort',3,14),
    ('Li-Ion 4383 mAh, non-removable',3,13),
    ('6.7 inches, 110.2 cm2 (~88.0% screen-to-body ratio) Super Retina XDR OLED, HDR10, Dolby Vision, 1000 nits (HBM), 2000 nits (peak)',3,5),
    ('128GB, 256GB, 512GB',3,11),
    ('4K@24/25/30/60fps, 1080p@25/30/60/120/240fps, HDR, Dolby Vision HDR (up to 60fps), stereo sound rec.',3,15),
    ('Intel Core Ultra 7 155H',5,1),
    ('Intel Arc Graphics',5,2),
    ('‎Windows 11 Home',5,3);
INSERT INTO product_attribute_value
    (value,product_id,product_attribute_id) VALUES
    ('12 Megapixel camera',5,9),
    ('‎Bluetooth, Wi-Fi',5,14),
    ('1920 x 1200 pixel',5,12),
    ('‎60 watts Lithium Battery',5,13),
    ('‎512 GB SSD',5,11),
    ('16 GB ‎LPDDR5x ‎6400 MHz',5,10),
    ('14.5 Inches',5,4),
    ('Qualcomm SM8650-AC Snapdragon 8 Gen 3 (4 nm)',8,1),
    ('Adreno 750 (1 GHz)',8,2),
    ('Android 14, up to 4 major Android upgrades, One UI 6.1.1',8,3),
    ('7.6 inches, 185.2 cm2 (~91.0% screen-to-body ratio)',8,4),
    ('50 MP, f/1.8, 23mm (wide), 1.0µm, dual pixel PDAF, OIS 10 MP, f/2.4, 66mm (telephoto), 1.0µm, PDAF, OIS, 3x optical zoom 12 MP, f/2.2, 123˚, 12mm (ultrawide), 1.12µm',8,8),
    ('4 MP, f/1.8, 26mm (wide), 2.0µm, under display Cover camera: 10 MP, f/2.2, 24mm (wide), 1/3", 1.22µm',8,9),
    ('6.1‑inch (diagonal) all‑screen OLED display',8,14),
    ('256 GB, 512 GB, 1TB',8,11),
    ('8K@30fps, 4K@60fps, 1080p@60/120/240fps (gyro-EIS), 720p@960fps (gyro-EIS), HDR10+',8,15),
    ('Li-Po 4400 mAh, non-removable',8,13),
    ('6.3" AMOLED - 153.5mm x 68.1mm x 12.1mm + 7.6" AMOLED - 153.5mm x 132.6mm x 5.6mm',8,5),
    ('iOS 17, upgradable to iOS 18',3,3),
    ('128GB 6GB RAM, 256GB 6GB RAM, 512GB 6GB RAM',3,10);
INSERT INTO product_attribute_value
    (value,product_id,product_attribute_id) VALUES
    ('Ultra Retina Tandem OLED, 120Hz, HDR10, Dolby Vision, 1000 nits (HBM), 1600 nits (peak)',6,1),
    ('Ultra Retina Tandem OLED, 120Hz, HDR10, Dolby Vision, 1000 nits (HBM), 1600 nits (peak)',6,2),
    ('256GB 8GB RAM, 512GB 8GB RAM, 1TB 16GB RAM, 2TB 16GB RAM',6,10),
    ('256GB 8GB RAM, 512GB 8GB RAM, 1TB 16GB RAM, 2TB 16GB RAM',6,11),
    ('iOS 17, upgradable to iOS 18',1,3),
    ('1179 x 2556 pixels, 19.5:9 ratio (~461 ppi density)',1,12),
    ('6.1 inches, 91.3 cm2 (~86.4% screen-to-body ratio) Ceramic Shield glass',1,4),
    ('iOS 17, upgradable to iOS 18',2,3),
    ('1668 x 2420 pixels, 3:2 ratio (~264 ppi density)',2,11),
    ('1179 x 2556 pixels, 19.5:9 ratio (~461 ppi density)',2,12),
    ('iPadOS 17.5.1, upgradable to iPadOS 18',4,2);


INSERT INTO category (id, "name", slug, parent_id, is_published, image_id) VALUES(1, 'Phone', 'phone', NULL, true, 1);
INSERT INTO category (id, "name", slug, parent_id, is_published, image_id) VALUES(2, 'Tablet', 'tablet', NULL, true, 2);
INSERT INTO category (id, "name", slug, parent_id, is_published, image_id) VALUES(3, 'Galaxy', 'galaxy', 1, true, 3);
INSERT INTO category (id, "name", slug, parent_id, is_published, image_id) VALUES(4, 'iPhone', 'iphone', 1, true, 4);
INSERT INTO category (id, "name", slug, parent_id, is_published, image_id) VALUES(5, 'Laptop', 'laptop', NULL, true, 5);
INSERT INTO category (id, "name", slug, parent_id, is_published, image_id) VALUES(6, 'Workstation', 'workstation', 5, true, 6);
INSERT INTO category (id, "name", slug, parent_id, is_published, image_id) VALUES(7, 'iPad', 'ipad', 2, true, 35);
INSERT INTO category (id, "name", slug, parent_id, is_published, image_id) VALUES(8, 'Galaxy Tab', 'galaxy-tab', 2, true, 36);
INSERT INTO category (id, "name", slug, parent_id, is_published, image_id) VALUES(9, 'Macbook', 'macbook', 5, true, 48);
ALTER SEQUENCE category_id_seq RESTART WITH 10;

INSERT INTO product_category
(id,display_order,is_featured_product,category_id,product_id) VALUES
(1,0,false,1,1),
(2,0,false,4,1),
(3,0,false,1,2),
(4,0,false,4,2),
(7,0,false,1,4),
(8,0,false,4,4),
(18,0,false,5,13),
(19,0,false,9,13),
(20,0,false,5,14),
(21,0,false,9,14),
(22,0,false,1,15),
(23,0,false,3,15),
(24,0,false,5,16),
(25,0,false,6,16),
(28,0,false,2,17),
(29,0,false,7,17),
(30,0,false,5,5),
(31,0,false,6,5),
(32,0,false,1,8),
(33,0,false,3,8),
(34,0,false,2,6),
(35,0,false,7,6);
ALTER SEQUENCE product_category_id_seq RESTART WITH 36;

INSERT INTO product_image (id, image_id, product_id)
VALUES (32, 8, 1);
INSERT INTO product_image (id, image_id, product_id)
VALUES (33, 9, 1);
INSERT INTO product_image (id, image_id, product_id)
VALUES (34, 10, 1);
INSERT INTO product_image (id, image_id, product_id)
VALUES (35, 11, 1);
INSERT INTO product_image (id, image_id, product_id)
VALUES (36, 13, 2);
INSERT INTO product_image (id, image_id, product_id)
VALUES (37, 14, 2);
INSERT INTO product_image (id, image_id, product_id)
VALUES (38, 15, 2);
INSERT INTO product_image (id, image_id, product_id)
VALUES (39, 16, 2);
INSERT INTO product_image (id, image_id, product_id)
VALUES (40, 18, 3);
INSERT INTO product_image (id, image_id, product_id)
VALUES (41, 19, 3);
INSERT INTO product_image (id, image_id, product_id)
VALUES (42, 20, 3);
INSERT INTO product_image (id, image_id, product_id)
VALUES (43, 21, 3);
INSERT INTO product_image (id, image_id, product_id)
VALUES (44, 23, 4);
INSERT INTO product_image (id, image_id, product_id)
VALUES (45, 24, 4);
INSERT INTO product_image (id, image_id, product_id)
VALUES (46, 25, 4);
INSERT INTO product_image (id, image_id, product_id)
VALUES (47, 26, 4);
INSERT INTO product_image (id, image_id, product_id)
VALUES (48, 28, 5);
INSERT INTO product_image (id, image_id, product_id)
VALUES (49, 29, 5);
INSERT INTO product_image (id, image_id, product_id)
VALUES (50, 31, 6);
INSERT INTO product_image (id, image_id, product_id)
VALUES (51, 32, 6);
INSERT INTO product_image (id, image_id, product_id)
VALUES (52, 33, 6);
INSERT INTO product_image (id, image_id, product_id)
VALUES (53, 34, 6);
INSERT INTO product_image (id, image_id, product_id)
VALUES (54, 38, 7);
INSERT INTO product_image (id, image_id, product_id)
VALUES (55, 39, 7);
INSERT INTO product_image (id, image_id, product_id)
VALUES (56, 40, 7);
INSERT INTO product_image (id, image_id, product_id)
VALUES (57, 42, 8);
INSERT INTO product_image (id, image_id, product_id)
VALUES (58, 43, 8);
INSERT INTO product_image (id, image_id, product_id)
VALUES (59, 44, 8);
INSERT INTO product_image (id, image_id, product_id)
VALUES (60, 45, 8);
INSERT INTO product_image (id, image_id, product_id)
VALUES (61, 46, 8);
INSERT INTO product_image (id, image_id, product_id)
VALUES (62, 47, 8);
INSERT INTO product_image (id, image_id, product_id)
VALUES (75, 50, 12);
INSERT INTO product_image (id, image_id, product_id)
VALUES (76, 51, 12);
INSERT INTO product_image (id, image_id, product_id)
VALUES (77, 52, 12);
INSERT INTO product_image (id, image_id, product_id)
VALUES (78, 53, 12);
INSERT INTO product_image (id, image_id, product_id)
VALUES (79, 56, 13);
INSERT INTO product_image (id, image_id, product_id)
VALUES (80, 57, 13);
INSERT INTO product_image (id, image_id, product_id)
VALUES (81, 58, 13);
INSERT INTO product_image (id, image_id, product_id)
VALUES (82, 60, 14);
INSERT INTO product_image (id, image_id, product_id)
VALUES (83, 61, 14);
INSERT INTO product_image (id, image_id, product_id)
VALUES (84, 62, 14);
INSERT INTO product_image (id, image_id, product_id)
VALUES (85, 63, 14);
INSERT INTO product_image (id, image_id, product_id)
VALUES (86, 65, 15);
INSERT INTO product_image (id, image_id, product_id)
VALUES (87, 66, 15);
INSERT INTO product_image (id, image_id, product_id)
VALUES (88, 67, 15);
INSERT INTO product_image (id, image_id, product_id)
VALUES (89, 68, 15);
INSERT INTO product_image (id, image_id, product_id)
VALUES (90, 69, 15);
INSERT INTO product_image (id, image_id, product_id)
VALUES (91, 70, 15);
INSERT INTO product_image (id, image_id, product_id)
VALUES (92, 72, 16);
INSERT INTO product_image (id, image_id, product_id)
VALUES (93, 73, 16);
INSERT INTO product_image (id, image_id, product_id)
VALUES (94, 74, 16);
INSERT INTO product_image (id, image_id, product_id)
VALUES (95, 75, 16);
INSERT INTO product_image (id, image_id, product_id)
VALUES (96, 77, 17);
INSERT INTO product_image (id, image_id, product_id)
VALUES (97, 78, 17);
INSERT INTO product_image (id, image_id, product_id)
VALUES (98, 79, 17);
INSERT INTO product_image (id, image_id, product_id)
VALUES (99, 80, 17);
ALTER SEQUENCE product_image_id_seq RESTART WITH 100;

INSERT INTO product_related (id, product_id, related_product_id)
VALUES (11, 3, 1),
       (12, 3, 2),
       (13, 4, 1),
       (14, 4, 3),
       (15, 4, 2),
       (16, 7, 6),
       (17, 8, 1),
       (18, 8, 3),
       (19, 8, 4),
       (20, 8, 2),
       (21, 12, 5),
       (22, 13, 6),
       (23, 13, 12),
       (24, 14, 12),
       (25, 14, 13),
       (26, 15, 1),
       (27, 15, 3),
       (28, 15, 4),
       (29, 15, 2),
       (30, 15, 8),
       (31, 16, 5),
       (32, 16, 12),
       (33, 16, 13),
       (34, 16, 14),
       (35, 17, 7),
       (36, 17, 6),
       (37, 5, 12),
       (38, 5, 13),
       (39, 5, 14),
       (40, 6, 7);
ALTER SEQUENCE product_related_id_seq RESTART WITH 41;