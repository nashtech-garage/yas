DELETE
FROM product_attribute_template;
DELETE
FROM product_attribute_value;
DELETE
FROM product_attribute;
DELETE
FROM product_attribute_group;
DELETE
FROM product_category;
DELETE
FROM product_image;
DELETE
FROM product_option_combination;
DELETE
FROM product_option_value;
DELETE
FROM product_option;
DELETE
FROM product_related;
DELETE
FROM product_template;
DELETE
FROM product;
DELETE
FROM brand;
DELETE
FROM category;

INSERT INTO brand (id, "name", slug, is_published)
VALUES (1, 'Apple', 'apple', true);
INSERT INTO brand (id, "name", slug, is_published)
VALUES (2, 'Samsung', 'samsung', true);
INSERT INTO brand (id, "name", slug, is_published)
VALUES (3, 'Dell', 'dell', true);
INSERT INTO brand (id, "name", slug, is_published)
VALUES (4, 'LG', 'lg', true);
ALTER SEQUENCE brand_id_seq RESTART WITH 5;

INSERT INTO product_attribute_group (id, "name")
VALUES (1, 'General');
INSERT INTO product_attribute_group (id, "name")
VALUES (2, 'Screen');
INSERT INTO product_attribute_group (id, "name")
VALUES (3, 'Connectivity');
INSERT INTO product_attribute_group (id, "name")
VALUES (4, 'Camera');
INSERT INTO product_attribute_group (id, "name")
VALUES (5, 'Memory');
ALTER SEQUENCE product_attribute_group_id_seq RESTART WITH 6;

INSERT INTO product_attribute (id, "name", product_attribute_group_id)
VALUES (1, 'CPU', 1);
INSERT INTO product_attribute (id, "name", product_attribute_group_id)
VALUES (2, 'GPU', 1);
INSERT INTO product_attribute (id, "name", product_attribute_group_id)
VALUES (3, 'OS', 1);
INSERT INTO product_attribute (id, "name", product_attribute_group_id)
VALUES (4, 'Screen Size', 2);
INSERT INTO product_attribute (id, "name", product_attribute_group_id)
VALUES (5, 'Panel', 2);
INSERT INTO product_attribute (id, "name", product_attribute_group_id)
VALUES (6, 'Bluetooth', 3);
INSERT INTO product_attribute (id, "name", product_attribute_group_id)
VALUES (7, 'NFC', 3);
INSERT INTO product_attribute (id, "name", product_attribute_group_id)
VALUES (8, 'Main Camera', 4);
INSERT INTO product_attribute (id, "name", product_attribute_group_id)
VALUES (9, 'Sub Camera', 4);
INSERT INTO product_attribute (id, "name", product_attribute_group_id)
VALUES (10, 'RAM', 5);
INSERT INTO product_attribute (id, "name", product_attribute_group_id)
VALUES (11, 'Storage', 5);
INSERT INTO product_attribute (id, "name", product_attribute_group_id)
VALUES (12, 'Screen Resolution', 2);
ALTER SEQUENCE product_attribute_id_seq RESTART WITH 13;

INSERT INTO product_template (id, "name")
VALUES (1, 'Sample Template');
ALTER SEQUENCE product_template_id_seq RESTART WITH 2;

INSERT INTO product_attribute_template (id, product_attribute_id, product_template_id, display_order)
VALUES (1, 1, 1, 0);
INSERT INTO product_attribute_template (id, product_attribute_id, product_template_id, display_order)
VALUES (2, 2, 1, 0);
INSERT INTO product_attribute_template (id, product_attribute_id, product_template_id, display_order)
VALUES (3, 10, 1, 0);
INSERT INTO product_attribute_template (id, product_attribute_id, product_template_id, display_order)
VALUES (4, 11, 1, 0);
INSERT INTO product_attribute_template (id, product_attribute_id, product_template_id, display_order)
VALUES (5, 4, 1, 0);
INSERT INTO product_attribute_template (id, product_attribute_id, product_template_id, display_order)
VALUES (6, 3, 1, 0);
ALTER SEQUENCE product_attribute_template_id_seq RESTART WITH 7;

INSERT INTO product_option (id, "name")
VALUES (1, 'COLOR');
INSERT INTO product_option (id, "name")
VALUES (2, 'CPU');
INSERT INTO product_option (id, "name")
VALUES (3, 'RAM');
INSERT INTO product_option (id, "name")
VALUES (4, 'OS');
INSERT INTO product_option (id, "name")
VALUES (5, 'Storage');
ALTER SEQUENCE product_option_id_seq RESTART WITH 6;

INSERT INTO product (id, description, gtin, has_options, is_allowed_to_order, is_featured, is_published,
                     is_visible_individually, meta_description, meta_keyword, meta_title, "name", price,
                     short_description, sku, slug, specification, thumbnail_media_id, brand_id, stock_tracking_enabled,
                     tax_included, stock_quantity, tax_class_id)
VALUES (1,
        'iPhone 15 brings you Dynamic Island, a 48MP Main camera, and USB-C—all in a durable color-infused glass and aluminum design.',
        'IP15', false, true, true, true, true,
        '<h3><strong>iPhone 15 brings you Dynamic Island, a 48MP Main camera, and USB-C—all in a durable color-infused glass and aluminum design.</strong></h3>',
        '', '', 'iPhone 15', 799.0,
        '<h3><strong>iPhone 15 brings you Dynamic Island, a 48MP Main camera, and USB-C—all in a durable color-infused glass and aluminum design.</strong></h3>',
        'IP15', 'iphone-15',
        '<ul><li><em>DYNAMIC ISLAND COMES TO IPHONE 15 — Dynamic Island bubbles up alerts and Live Activities&nbsp;— so you don’t miss them while you’re doing something else. You can see who’s calling, track your next ride, check your flight status, and so much more.</em></li><li><em>INNOVATIVE DESIGN — iPhone 15 Plus features a durable color-infused glass and aluminum design. It’s splash, water, and dust resistant.¹ The Ceramic Shield front is tougher than any smartphone glass. And the 6.7" Super Retina XDR display² is up to 2x brighter in the sun compared to iPhone 14.</em></li><li><em>48MP MAIN CAMERA WITH 2X TELEPHOTO — The 48MP Main camera shoots in super high-resolution. So it’s easier than ever to take standout photos with amazing detail. The 2x optical-quality Telephoto lets you frame the perfect close-up.</em></li><li><em>NEXT-GENERATION PORTRAITS — Capture portraits with dramatically more detail and color. Just tap to shift the focus between subjects — even after you take the shot.</em></li><li><em>POWERHOUSE A16 BIONIC CHIP — The superfast chip powers advanced features like computational photography, fluid Dynamic Island transitions, and Voice Isolation for phone calls. And A16 Bionic is incredibly efficient to help deliver great all-day battery life.³</em></li><li><em>USB-C CONNECTIVITY — The USB-C connector lets you&nbsp;charge your Mac or iPad with the same cable&nbsp;you use to charge iPhone 15 Plus. You can even use iPhone 15 Plus to charge Apple Watch or AirPods.</em></li><li><em>VITAL SAFETY FEATURES — If your car breaks down when you’re off the grid, you can get help with Roadside Assistance via satellite.⁴ And if you need emergency services and you don’t have cell service or Wi-Fi, you can use Emergency SOS via satellite.⁴ With Crash Detection, iPhone can detect a severe car crash and call for help if you can’t.⁵</em></li><li><em>DESIGNED TO MAKE A DIFFERENCE — iPhone comes with privacy protections that help keep you in control of your data. It’s made from more recycled materials to minimize environmental impact. And it has built-in features that make iPhone more accessible to all.</em></li><li><em>COMES WITH APPLECARE WARRANTY — Every iPhone comes with a one-year limited warranty and up to 90 days of complimentary technical support. Get AppleCare+ or AppleCare+ with Theft and Loss to extend your coverage.</em></li><li><em>¹iPhone 15, iPhone 15 Plus, iPhone 15 Pro, and iPhone 15 Pro Max are splash, water, and dust resistant and were tested under controlled laboratory conditions with a rating of IP68 under IEC standard 60529 (maximum depth of 6 meters up to 30 minutes).</em></li><li><em>Splash, water, and dust resistance are not permanent conditions. Resistance might decrease as a result of normal wear. Do not attempt to charge a wet iPhone; refer to the user guide for cleaning and drying instructions. Liquid damage not covered under warranty.</em></li><li><em>²The display has rounded corners. When measured as a standard rectangle, the screen is 6.12 inches (iPhone 15 Pro, iPhone 15) or 6.69 inches (iPhone 15 Pro Max, iPhone 15 Plus) diagonally. Actual viewable area is less.</em></li><li><em>³Battery life varies by use and configuration; see&nbsp;apple.com/batteries&nbsp;for more information.</em></li><li><em>⁴Service is included for free for two years with the activation of any iPhone 15 model. Connection and response times vary based on location, site conditions, and other factors. See support.apple.com/kb/HT213885 for more information.</em></li><li><em>⁵iPhone 15 and iPhone 15 Pro can detect a severe car crash and call for help. Requires a cellular connection or Wi-Fi Calling.</em></li></ul>',
        7, 1, false, true, 10, 1);
INSERT INTO product (id, description, gtin, has_options, is_allowed_to_order, is_featured, is_published,
                     is_visible_individually, meta_description, meta_keyword, meta_title, "name", price,
                     short_description, sku, slug, specification, thumbnail_media_id, brand_id, stock_tracking_enabled,
                     tax_included, stock_quantity, tax_class_id)
VALUES (2,
        'iPhone 15 Pro. Forged in titanium and featuring the groundbreaking A17 Pro chip, a customizable Action button, and a more versatile Pro camera system. ',
        'IP15PRO', false, true, true, true, true,
        '<h3><strong style="color: rgb(4, 12, 19);">iPhone 15 Pro. Forged in titanium and featuring the groundbreaking A17 Pro chip, a customizable Action button, and&nbsp;a more versatile Pro camera system.&nbsp;</strong></h3>',
        '', '', 'iPhone 15 Pro', 899.0,
        '<h3><strong style="color: rgb(4, 12, 19);">iPhone 15 Pro. Forged in titanium and featuring the groundbreaking A17 Pro chip, a customizable Action button, and&nbsp;a more versatile Pro camera system.&nbsp;</strong></h3>',
        'IP15PRO', 'iphone-15-pro',
        '<ul><li><em>FORGED IN TITANIUM&nbsp;— iPhone 15 Pro has a strong and light aerospace-grade titanium design with a textured matte-glass back. It also features a Ceramic Shield front that’s tougher than any smartphone glass. And it’s splash, water, and dust resistant.¹</em></li><li><em>ADVANCED DISPLAY — The 6.1” Super Retina XDR display² with ProMotion ramps up refresh rates to 120Hz&nbsp;when you need exceptional graphics performance. Dynamic Island bubbles up alerts and Live Activities. Plus, with Always-On display, your Lock Screen stays glanceable, so you don’t have to tap it to stay in the know.</em></li><li><em>GAME-CHANGING A17 PRO CHIP — A Pro-class GPU makes mobile games feel so immersive, with rich environments and realistic characters. A17 Pro is also incredibly efficient and helps to deliver amazing all-day battery life.³</em></li><li><em>POWERFUL PRO CAMERA SYSTEM — Get incredible framing flexibility with 7 pro lenses. Capture super high-resolution photos with more color and detail using the 48MP Main camera. And take sharp close-ups from far away with the 3x Telephoto camera on iPhone 15 Pro.</em></li><li><em>CUSTOMIZABLE ACTION BUTTON — Action button is a fast track to your favorite feature. Just set the one you want, like Silent mode, Camera, Voice Memo, Shortcut, and more. Then press and hold to launch the action.</em></li><li><em>PRO CONNECTIVITY — The new USB-C connector lets you charge your Mac or iPad with the same cable you use to charge iPhone 15 Pro. With USB 3, you get a huge leap in data transfer speeds.⁴ And you can download files up to 2x faster using Wi-Fi 6E.⁵</em></li><li><em>VITAL SAFETY FEATURES — If your car breaks down when you’re off the grid, you can get help with Roadside Assistance via satellite.⁶ And if you need emergency services and you don’t have cell service or Wi-Fi, you can use Emergency SOS via satellite.⁶ With Crash Detection, iPhone can detect a severe car crash and call for help if you can’t.⁷</em></li><li><em>DESIGNED TO MAKE A DIFFERENCE — iPhone comes with privacy protections that help keep you in control of your data. It’s made from more recycled materials to minimize environmental impact. And it has built-in features that make iPhone more accessible to all.</em></li><li><em>COMES WITH APPLECARE WARRANTY — Every iPhone comes with a one-year limited warranty and up to 90 days of complimentary technical support. Get AppleCare+ or AppleCare+ with Theft and Loss to extend your coverage.</em></li><li><em>¹iPhone 15, iPhone 15 Plus, iPhone 15 Pro, and iPhone 15 Pro Max are splash, water, and dust resistant and were tested under controlled laboratory conditions with a rating of IP68 under IEC standard 60529 (maximum depth of 6 meters up to 30 minutes).</em></li><li><em>Splash, water, and dust resistance are not permanent conditions. Resistance might decrease as a result of normal wear. Do not attempt to charge a wet iPhone; refer to the user guide for cleaning and drying instructions. Liquid damage not covered under warranty.</em></li><li><em>²The display has rounded corners. When measured as a standard rectangle, the screen is 6.12 inches (iPhone 15 Pro, iPhone 15) or 6.69 inches (iPhone 15 Pro Max, iPhone 15 Plus) diagonally. Actual viewable area is less.</em></li><li><em>³Battery life varies by use and configuration; see&nbsp;apple.com/batteries&nbsp;for more information.</em></li><li><em>⁴USB 3 cable with 10Gb/s speed required for up to 20x faster transfers on iPhone 15 Pro models.</em></li><li><em>⁵Wi-Fi 6E available in countries and regions where supported.</em></li><li><em>⁶Service is included for free for two years with the activation of any iPhone 15 model. Connection and response times vary based on location, site conditions, and other factors. See support.apple.com/kb/HT213885 for more information.</em></li><li><em>⁷iPhone 15 and iPhone 15 Pro can detect a severe car crash and call for help. Requires a cellular connection or Wi-Fi Calling.</em></li></ul>',
        12, 1, false, true, 10, 1);
INSERT INTO product (id, description, gtin, has_options, is_allowed_to_order, is_featured, is_published,
                     is_visible_individually, meta_description, meta_keyword, meta_title, "name", price,
                     short_description, sku, slug, specification, thumbnail_media_id, brand_id, stock_tracking_enabled,
                     tax_included, stock_quantity, tax_class_id)
VALUES (3,
        'iPhone 15 Plus brings you Dynamic Island, a 48MP Main camera, and USB-C—all in a durable color-infused glass and aluminum design.',
        'IP15PLUS', false, true, true, true, true,
        '<h3><strong style="color: rgb(4, 12, 19);">iPhone 15 Plus brings you Dynamic Island, a 48MP Main camera, and USB-C—all in a durable color-infused glass and aluminum design.</strong></h3>',
        '', '', 'iPhone 15 Plus', 859.0,
        '<h3><strong style="color: rgb(4, 12, 19);">iPhone 15 Plus brings you Dynamic Island, a 48MP Main camera, and USB-C—all in a durable color-infused glass and aluminum design.</strong></h3>',
        'IP15PLUS', 'iphone-15-plus',
        '<ul><li><em>DYNAMIC ISLAND COMES TO IPHONE 15 — Dynamic Island bubbles up alerts and Live Activities&nbsp;— so you don’t miss them while you’re doing something else. You can see who’s calling, track your next ride, check your flight status, and so much more.</em></li><li><em>INNOVATIVE DESIGN — iPhone 15 Plus features a durable color-infused glass and aluminum design. It’s splash, water, and dust resistant.¹ The Ceramic Shield front is tougher than any smartphone glass. And the 6.7" Super Retina XDR display² is up to 2x brighter in the sun compared to iPhone 14.</em></li><li><em>48MP MAIN CAMERA WITH 2X TELEPHOTO — The 48MP Main camera shoots in super high-resolution. So it’s easier than ever to take standout photos with amazing detail. The 2x optical-quality Telephoto lets you frame the perfect close-up.</em></li><li><em>NEXT-GENERATION PORTRAITS — Capture portraits with dramatically more detail and color. Just tap to shift the focus between subjects — even after you take the shot.</em></li><li><em>POWERHOUSE A16 BIONIC CHIP — The superfast chip powers advanced features like computational photography, fluid Dynamic Island transitions, and Voice Isolation for phone calls. And A16 Bionic is incredibly efficient to help deliver great all-day battery life.³</em></li><li><em>USB-C CONNECTIVITY — The USB-C connector lets you&nbsp;charge your Mac or iPad with the same cable&nbsp;you use to charge iPhone 15 Plus. You can even use iPhone 15 Plus to charge Apple Watch or AirPods.</em></li><li><em>VITAL SAFETY FEATURES — If your car breaks down when you’re off the grid, you can get help with Roadside Assistance via satellite.⁴ And if you need emergency services and you don’t have cell service or Wi-Fi, you can use Emergency SOS via satellite.⁴ With Crash Detection, iPhone can detect a severe car crash and call for help if you can’t.⁵</em></li><li><em>DESIGNED TO MAKE A DIFFERENCE — iPhone comes with privacy protections that help keep you in control of your data. It’s made from more recycled materials to minimize environmental impact. And it has built-in features that make iPhone more accessible to all.</em></li><li><em>COMES WITH APPLECARE WARRANTY — Every iPhone comes with a one-year limited warranty and up to 90 days of complimentary technical support. Get AppleCare+ or AppleCare+ with Theft and Loss to extend your coverage.</em></li><li><em>¹iPhone 15, iPhone 15 Plus, iPhone 15 Pro, and iPhone 15 Pro Max are splash, water, and dust resistant and were tested under controlled laboratory conditions with a rating of IP68 under IEC standard 60529 (maximum depth of 6 meters up to 30 minutes).</em></li><li><em>Splash, water, and dust resistance are not permanent conditions. Resistance might decrease as a result of normal wear. Do not attempt to charge a wet iPhone; refer to the user guide for cleaning and drying instructions. Liquid damage not covered under warranty.</em></li><li><em>²The display has rounded corners. When measured as a standard rectangle, the screen is 6.12 inches (iPhone 15 Pro, iPhone 15) or 6.69 inches (iPhone 15 Pro Max, iPhone 15 Plus) diagonally. Actual viewable area is less.</em></li><li><em>³Battery life varies by use and configuration; see&nbsp;apple.com/batteries&nbsp;for more information.</em></li><li><em>⁴Service is included for free for two years with the activation of any iPhone 15 model. Connection and response times vary based on location, site conditions, and other factors. See support.apple.com/kb/HT213885 for more information.</em></li><li><em>⁵iPhone 15 and iPhone 15 Pro can detect a severe car crash and call for help. Requires a cellular connection or Wi-Fi Calling.</em></li></ul>',
        17, 1, false, true, 10, 1);
INSERT INTO product (id, description, gtin, has_options, is_allowed_to_order, is_featured, is_published,
                     is_visible_individually, meta_description, meta_keyword, meta_title, "name", price,
                     short_description, sku, slug, specification, thumbnail_media_id, brand_id, stock_tracking_enabled,
                     tax_included, stock_quantity, tax_class_id)
VALUES (4,
        'iPhone 15 Pro Max. Forged in titanium and featuring the groundbreaking A17 Pro chip, a customizable Action button, and the most powerful iPhone camera system ever. ',
        'IP15PROMAX', false, true, true, true, true,
        '<h3><strong style="color: rgb(4, 12, 19);">iPhone 15 Pro Max. Forged in titanium and featuring the groundbreaking A17 Pro chip, a customizable Action button, and the most powerful iPhone camera system ever.&nbsp;</strong></h3>',
        '', '', 'iPhone 15 Pro Max', 1199.0,
        '<h3><strong style="color: rgb(4, 12, 19);">iPhone 15 Pro Max. Forged in titanium and featuring the groundbreaking A17 Pro chip, a customizable Action button, and the most powerful iPhone camera system ever.&nbsp;</strong></h3>',
        'IP15PROMAX', 'iphone-15-pro-max',
        '<ul><li><em>FORGED IN TITANIUM&nbsp;— iPhone 15 Pro Max has a strong and light aerospace-grade titanium design with a textured matte-glass back. It also features a Ceramic Shield front that’s tougher than any smartphone glass. And it’s splash, water, and dust resistant.¹</em></li><li><em>ADVANCED DISPLAY — The 6.7” Super Retina XDR display² with ProMotion ramps up refresh rates to 120Hz&nbsp;when you need exceptional graphics performance. Dynamic Island bubbles up alerts and Live Activities. Plus, with Always-On display, your Lock Screen stays glanceable, so you don’t have to tap it to stay in the know.</em></li><li><em>GAME-CHANGING A17 PRO CHIP — A Pro-class GPU makes mobile games feel so immersive, with rich environments and realistic characters. A17 Pro is also incredibly efficient and helps to deliver amazing all-day battery life.³</em></li><li><em>POWERFUL PRO CAMERA SYSTEM — Get incredible framing flexibility with 7 pro lenses. Capture super high-resolution photos with more color and detail using the 48MP Main camera. And take sharper close-ups from farther away with the 5x Telephoto camera on iPhone 15 Pro Max.</em></li><li><em>CUSTOMIZABLE ACTION BUTTON — Action button is a fast track to your favorite feature. Just set the one you want, like Silent mode, Camera, Voice Memo, Shortcut, and more. Then press and hold to launch the action.</em></li><li><em>PRO CONNECTIVITY — The new USB-C connector lets you charge your Mac or iPad with the same cable you use to charge iPhone 15 Pro Max. With USB 3, you get a huge leap in data transfer speeds.⁴ And you can download files up to 2x faster using Wi-Fi 6E.⁵</em></li><li><em>VITAL SAFETY FEATURES — If your car breaks down when you’re off the grid, you can get help with Roadside Assistance via satellite.⁶ And if you need emergency services and you don’t have cell service or Wi-Fi, you can use Emergency SOS via satellite.⁶ With Crash Detection, iPhone can detect a severe car crash and call for help if you can’t.⁷</em></li><li><em>DESIGNED TO MAKE A DIFFERENCE — iPhone comes with privacy protections that help keep you in control of your data. It’s made from more recycled materials to minimize environmental impact. And it has built-in features that make iPhone more accessible to all.</em></li><li><em>COMES WITH APPLECARE WARRANTY — Every iPhone comes with a one-year limited warranty and up to 90 days of complimentary technical support. Get AppleCare+ or AppleCare+ with Theft and Loss to extend your coverage.</em></li><li><em>¹iPhone 15, iPhone 15 Plus, iPhone 15 Pro, and iPhone 15 Pro Max are splash, water, and dust resistant and were tested under controlled laboratory conditions with a rating of IP68 under IEC standard 60529 (maximum depth of 6 meters up to 30 minutes).</em></li><li><em>Splash, water, and dust resistance are not permanent conditions. Resistance might decrease as a result of normal wear. Do not attempt to charge a wet iPhone; refer to the user guide for cleaning and drying instructions. Liquid damage not covered under warranty.</em></li><li><em>²The display has rounded corners. When measured as a standard rectangle, the screen is 6.12 inches (iPhone 15 Pro, iPhone 15) or 6.69 inches (iPhone 15 Pro Max, iPhone 15 Plus) diagonally. Actual viewable area is less.</em></li><li><em>³Battery life varies by use and configuration; see&nbsp;apple.com/batteries&nbsp;for more information.</em></li><li><em>⁴USB 3 cable with 10Gb/s speed required for up to 20x faster transfers on iPhone 15 Pro models.</em></li><li><em>⁵Wi-Fi 6E available in countries and regions where supported.</em></li><li><em>⁶Service is included for free for two years with the activation of any iPhone 15 model. Connection and response times vary based on location, site conditions, and other factors. See support.apple.com/kb/HT213885 for more information.</em></li><li><em>⁷iPhone 15 and iPhone 15 Pro can detect a severe car crash and call for help. Requires a cellular connection or Wi-Fi Calling.</em></li></ul>',
        22, 1, false, true, 10, 1);
INSERT INTO product (id, description, gtin, has_options, is_allowed_to_order, is_featured, is_published,
                     is_visible_individually, meta_description, meta_keyword, meta_title, "name", price,
                     short_description, sku, slug, specification, thumbnail_media_id, brand_id, stock_tracking_enabled,
                     tax_included, stock_quantity, tax_class_id)
VALUES (5,
        'Unlock non-stop creativity with the new Dell XPS 14, a perfect balance of go-anywhere mobility and high performance.​ ',
        'DELL-9440', false, true, true, true, true,
        '<h2><strong style="color: rgb(4, 12, 19);">Unlock non-stop creativity with the new Dell XPS 14, a perfect balance of go-anywhere mobility and high performance.​&nbsp;</strong></h2>',
        '', '', 'Laptop Dell XPS 14 9440', 2299.0,
        '<h2><strong style="color: rgb(4, 12, 19);">Unlock non-stop creativity with the new Dell XPS 14, a perfect balance of go-anywhere mobility and high performance.​&nbsp;</strong></h2>',
        'DELL-9440', 'laptop-dell-xps-14-9440',
        '<h4>Windows 11 Home</h4><ul><li>Windows 11 has all the power and security of Windows 10 with a redesigned and refreshed look. It also comes with new tools, sounds, and apps. Every detail has been considered. All of it comes together to bring you a refreshing experience on your PC</li></ul><h4>14.5" 3.2K (3200 x 2000) OLED InfinityEdge Touch Display</h4><ul><li>Natural finger-touch navigation makes the most of Windows 11. 3200 x 2000 resolution showcases your games and HD movies with impressive color and clarity. Antireflective finish reduces eyestrain and widens the field of view.</li></ul><h4>Unlock intelligent performance with Intel Core Ultra 7 processors</h4><ul><li>Get a productivity boost with a dedicated engine that helps unlock AI experiences on the PC and high-performance low power processing. Enjoy superior performance with less drain on battery, so you can do more for longer while unplugged.</li></ul><h4>32GB system memory for intense multitasking and gaming</h4><ul><li>Reams of high-bandwidth LPDDR5X RAM to smoothly run your graphics-heavy PC games and video-editing applications, as well as numerous programs and browser tabs all at once.</li></ul><h4>1TB M.2 PCIe NVMe Solid State Drive</h4><ul><li>While offering less storage space than a hard drive, a flash-based SSD has no moving parts, resulting in faster start-up times and data access, no noise, and reduced heat production and power draw on the battery.</li></ul><h4>Intel Arc Graphics</h4><ul><li>On-processor graphics with shared video memory provide everyday image quality for Internet use, basic photo editing and casual gaming. Optimized to use less power.</li></ul><h4>The best overall laptop experience with Intel Evo Edition</h4><ul><li>Get the best of Intel Core Ultra processors in an Intel Evo Edition laptop that is sleek and light for a better on-the-go experience, and charges fast and gives you superior performance with battery that lasts all day.</li></ul><h4>Vivid Color</h4><ul><li>Crisp visuals and true-to-life color with 14.5-inch touch-capable InfinityEdge display with up to 3.2K+ resolution.</li></ul><h4>Windows Copilot</h4><ul><li>Your own personal AI assistant built right in to do the heavy-lifting so you can do the extraordinary. With Copilot in Windows get real answers, inspiration, and solutions for your questions, projects, and to-do-list. Requires Microsoft account sign in.</li></ul><h4>Comfortable Keyboard</h4><ul><li>Larger, deeper, touch-friendly keycaps and less space between keys make typing more efficient.</li></ul><h4>Touch Function Row</h4><ul><li>Toggle between media and function keys easily and bring forward only the icons you want.</li></ul><h4>Seamless Glass Touchpad</h4><ul><li>A haptic touchpad provides precise responsive feedback. Sleek glass across the palm rest feels friendlier to the touch.</li><li>Note: this laptop does not include a DVD drive.</li><li>Intel, Pentium, Celeron, Core, Atom, Ultrabook, Intel Inside and the Intel Inside logo are trademarks or registered trademarks of Intel Corporation or its subsidiaries in the United States and other countries.</li></ul>',
        27, 3, false, true, 10, 1);
INSERT INTO product (id, description, gtin, has_options, is_allowed_to_order, is_featured, is_published,
                     is_visible_individually, meta_description, meta_keyword, meta_title, "name", price,
                     short_description, sku, slug, specification, thumbnail_media_id, brand_id, stock_tracking_enabled,
                     tax_included, stock_quantity, tax_class_id)
VALUES (6, NULL, 'IPAD-PRO-11', false, true, true, true, true,
        '<h3><strong style="color: rgb(4, 12, 19);">The new iPad Pro is impossibly thin, featuring outrageous performance with the Apple M4 chip, a breakthrough Ultra Retina XDR display, and superfast Wi-Fi 6E. </strong></h3>',
        '', '', 'iPad Pro 11 M4', 949.0,
        '<h3><strong style="color: rgb(4, 12, 19);">The new iPad Pro is impossibly thin, featuring outrageous performance with the Apple M4 chip, a breakthrough Ultra Retina XDR display, and superfast Wi-Fi 6E. </strong></h3>',
        'IPAD-PRO-11', 'ipad-pro-11-m4',
        '<ul><li>11-INCH ULTRA RETINA XDR DISPLAY—Ultra Retina XDR delivers extreme brightness and contrast, exceptional color accuracy, and features advanced technologies like ProMotion, P3 wide color, and True Tone. Plus a nano-texture glass option is available in 1TB and 2TB configurations.⁴</li><li>BUILT FOR APPLE INTELLIGENCE: Apple Intelligence helps you write, express yourself, and get things done effortlessly. It draws on your personal context while setting brand-new standard for privacy in AI. Coming in beta this fall.⁷</li><li>PERFORMANCE AND STORAGE—Up to 10-core CPU in the M4 chip delivers powerful performance, while the 10‑core GPU provides blazing-fast graphics. And with all-day battery life, you can do everything you can imagine on iPad Pro.⁵ Up to 2TB of storage means you can store everything from apps to large files like 4K video.⁶</li><li>IPADOS + APPS—iPadOS makes iPad more productive, intuitive, and versatile. With iPadOS, run multiple apps at once, use Apple Pencil to write in any text field with Scribble, and edit and share photos. Stage Manager makes multitasking easy with resizable, overlapping apps and external display support. iPad Pro comes with essential apps like Safari, Messages, and Keynote, with over a million more apps available on the App Store.</li><li>APPLE PENCIL AND MAGIC KEYBOARD FOR IPAD PRO—Apple Pencil Pro transforms iPad Pro into an immersive drawing canvas and the world’s best note‑taking device. Apple Pencil (USB-C) is also compatible with iPad Pro. Magic Keyboard for iPad Pro features a thin and light design, great typing experience, and a built‑in glass trackpad with haptic feedback, while doubling as a protective cover for iPad. Accessories sold separately.¹</li><li>ADVANCED CAMERAS—iPad Pro features a landscape 12MP Ultra Wide front camera that supports Center Stage for videoconferencing or epic Portrait mode selfies. The 12MP Wide back camera with adaptive True Tone flash is great for capturing photos or 4K video with ProRes support. Four studio-quality microphones and a four-speaker audio system provide rich audio. And AR experiences are enhanced with the LiDAR Scanner to capture a depth map of any space.</li><li>CONNECTIVITY—Wi-Fi 6E² gives you fast wireless connections. Work from almost anywhere with quick transfers of photos, documents, and large video files. Connect to external displays, drives, and more using the USB-C connector with support for Thunderbolt / USB 4.</li><li>UNLOCK AND PAY WITH FACE ID—Unlock your iPad Pro, securely authenticate purchases, sign in to apps, and more—all with just a glance.</li></ul><p><br></p>',
        30, 1, false, true, 10, 1);
INSERT INTO product (id, description, gtin, has_options, is_allowed_to_order, is_featured, is_published,
                     is_visible_individually, meta_description, meta_keyword, meta_title, "name", price,
                     short_description, sku, slug, specification, thumbnail_media_id, brand_id, stock_tracking_enabled,
                     tax_included, stock_quantity, tax_class_id)
VALUES (7,
        '<p><span style="color: rgb(4, 12, 19);">Meet Galaxy Tab S9 FE+, the powerful tablet that’s made just for you. A universe of possibilities awaits with Tab S9 FE+.&nbsp;</span></p>',
        'samsung-galaxy-tab-s9', false, true, true, true, true, '', '', '', 'Samsung - Galaxy Tab S9', 559.0,
        'Galaxy AI is here. Search like never before¹, let transcript assist² take the notes for you, format your notes into a clear summary,³ and effortlessly edit your photos⁴ -all from your tablet, all with AI.',
        'samsung-galaxy-tab-s9', 'samsung-galaxy-tab-s9',
        '<h4>A screen for all your passions</h4><ul><li>Be blown away by the large 12.4" screen. There’s plenty of room to follow your passions, whether you''re taking an online photography class, unwinding with cat videos or playing your favorite word game. Plus, dual speakers make everything sound amazing.¹</li></ul><h4>Ready for almost anything</h4><ul><li>Be ready for unexpected moments with a durable water-and dust-resistant design.² Galaxy Tab S9 FE+ is one of the only IP68-rated tablets on the market. It’s built to last wherever you use it, making it a great choice for first-time tablet buyers.</li></ul><h4>A battery that keeps you in charge</h4><ul><li>With a tablet this powerful, portable and fun, you’ll never want to put it down. Go up to 20 hours³ with a long-lasting battery. Need energy quick? Get a full charge in less than an hour and a half with Super Fast Charging.⁴</li></ul><h4>Power for every passion</h4><ul><li>Have plenty of power to get things done. The latest Exynos chipset helps you sail through your passions smoothly. Cross off your to-do list. Unwind with that new cooking show.</li><li>Take on the next mission in your favorite game. Whatever you’re doing, Galaxy Tab S9 FE+ makes for rich experiences.</li></ul><h4>Smart tech, beautifully made</h4><ul><li>Do it all in style. With four inspiring colors to choose from, Galaxy Tab S9 FE+ complements your personality perfectly. Plus, with a refined design that’s sleek and lightweight, you’ll be able to show off your great taste anywhere you use your Tab.</li></ul><h4>Pen-point precision</h4><ul><li>There’s virtually no limit to what you can do with S Pen. Write notes by hand. Draw a comic strip. Mark up a document. S Pen features a responsive design that feels like you’re writing on actual paper so nothing gets in your way when your imagination is flowing.</li></ul><h4>There’s room for that</h4><ul><li>Keep what you need to unwind however you want with up to 256GB⁵ of storage. That’s plenty of space for your favorite photos, apps and music. Need even more room? Add up to 1TB more with a microSD card.⁶</li></ul><h4>Cameras built for video chat</h4><ul><li>Collaborate effortlessly with a camera that automatically adjusts to keep the focus on you. Plus, with a large display that can open up to three apps at once, you can multitask while you video chat. Productivity never looked so good.⁷</li><li>¹Measured diagonally, the Galaxy Tab S9 FE+ screen size is 12.4" in the full rectangle and 12.3" accounting for the rounded corners. Actual viewable area is less due to the rounded corners.</li><li>²Consistent with IP68 rating, water resistant in up to 5 feet of fresh water for up to 30 minutes. Rinse residue / dry after wet.</li><li>³Based on local, 720p resolution video playback, default video player (full screen), 162 nit brightness, earbud volume 7, Wi-Fi and mobile network off. Results vary with settings, usage and other factors.</li><li>⁴Requires 45W wall charger (sold separately). Use only Samsung-approved charger and USB-C cable. To avoid injury or damage to your device, do not use incompatible, worn or damaged batteries, chargers or cables.</li><li>⁵Portion of storage / memory occupied by existing content.</li><li>⁶MicroSD card sold separately.</li><li>⁷Only in 16:9 FHD resolution.</li></ul><p><br></p>',
        37, 2, false, true, 10, 1);
INSERT INTO product (id, description, gtin, has_options, is_allowed_to_order, is_featured, is_published,
                     is_visible_individually, meta_description, meta_keyword, meta_title, "name", price,
                     short_description, sku, slug, specification, thumbnail_media_id, brand_id, stock_tracking_enabled,
                     tax_included, stock_quantity, tax_class_id)
VALUES (8,
        '<p>Do more than ever before with the game changing power of Galaxy AI on the expansive screen of Galaxy Z Fold6.</p>',
        'samsung-galaxy-z-fold6', false, true, true, true, true, '<p><br></p>', '', '', 'Samsung Galaxy Z Fold6',
        2099.0,
        'Galaxy AI is here. Search like never before¹, let transcript assist² take the notes for you, format your notes into a clear summary,³ and effortlessly edit your photos⁴ -all from your tablet, all with AI.',
        'samsung-galaxy-z-fold6', 'samsung-galaxy-z-fold6',
        '<h4>Circle it. Find it. See it.</h4><ul><li>Make huge discoveries when you use Circle to Search¹ with Google on the large screen of Galaxy Z Fold6. Circle those shoes you saw on your feed and easily see where to buy them. Search is easier than ever on an expansive screen.</li></ul><h4>Real-time translations on a big screen–it’s a big deal</h4><ul><li>Say bye to being lost in translation and hi to fluency in up to 16 languages when you use Interpreter with Galaxy AI.² While in FlexMode, your phone displays translations on both sides of the screen for easy in-person conversation.³</li></ul><h4>Smart AI edits. Brilliant photos.</h4><ul><li>Editing photos is even easier on the large screen of Galaxy Z Fold6. See more detail as you instantly fix imperfections, move and remove objects and enhance colors using AI² smart tools like Generative Edit and more.⁴</li></ul><h4>Immersive screen. Impressive graphics.</h4><ul><li>Level up your screen and level up your gaming experience. Totally immerse yourself with Galaxy Z Fold6 thanks to a huge screen, a lightning-fast processor and incredibly realistic graphics.</li></ul><h4>Shop, scroll and stream —all on one screen</h4><ul><li>The expansive screen of Galaxy Z Fold6 opens up multiple possibilities. Edit, scroll and organize. Or chat, shop and stream —all at the same time on up to three windows. When it comes to viewing and doing more, Galaxy Z Fold6 is built to handle more.⁵</li></ul><h4>Summarize in the blink of an AI</h4><ul><li>Simplify your day-to-day using Note Assist with Galaxy AI.² Jot thoughts down and watch as Note Assist⁶ automatically creates shopping lists, organizes your notes and summarizes lengthy reads. Then, view it all with ease on the large screen of Galaxy Z Fold6.</li></ul><h4>Multiple ways to tackle your to-dos</h4><ul><li>With a convenient outer screen and an expansive inner screen, the versatility of Galaxy Z Fold6 makes it feel like two phones in one. Unfold it to tackle your main to-dos or close it and text friends on the comfortable external display. However you do your day, Galaxy Z Fold6 is fun to use, inside and out.</li></ul><h4>Worry-free unfolding</h4><ul><li>It folds up and holds up. Galaxy Z Fold6 is built tough with Gorilla® Glass Victus® 2, IP48 water resistance¹⁰ and a pre-installed screen protector so you can unfold worry-free. Tested and verified for at least 200,000 folds (or approximately 10 years of use).²⁰</li></ul><h4>Huge screen. Huge possibilities.</h4><ul><li>Do more easily and have more space to do it when you use Galaxy AI² on Galaxy Z Fold6. Quickly organize your shopping list, enhance your photos on the fly and a whole lot more on an expansive screen that lets you expand the possibilities.⁶ ¹²</li></ul><h4>Two devices teaming up for your health</h4><ul><li>Track your health on the go with your Galaxy Watch Ultra and get deeper insights on your Galaxy Z Fold6. By pairing the two, get personalized health tracking from your wrist and then see results and actionable goals on the large screen of your phone.² ⁷</li></ul><h4>Phone to ear —translations you can hear</h4><ul><li>Get lost in conversation, not translation, when you pair Buds3 Pro with Galaxy Z Fold6 to hear live translations⁸ in your ear. Using Interpreter with Galaxy AI, you can hear what the person in front of you is saying in your language, even while they speak another. Plus, with the epic sound quality of Buds3 Pro,⁹ their voice translations come through loud and clear.</li></ul><h4>Slim. Light. Bold.</h4><ul><li>Open or closed, enjoy a slim design that’s light on weight and heavy on style. This fashionable foldable allows you to take big screen convenience wherever you go with ease.</li></ul><h4>Bring your Galaxy AI experience to your PC screen</h4><ul><li>Access the convenient Galaxy AI capabilities of your Galaxy Z Fold6 on your PC’s larger screen.¹³ Translate texts in real time using Chat Assist with Galaxy AI. Move or remove objects in photos with ease using Photo Assist.² And find things fast using Circle to Search with Google.¹⁴</li></ul><h4>Made for jotting big ideas on a big screen</h4><ul><li>Use your S Pen¹⁵ and navigate the huge display of Galaxy Z Fold6 with more control. Sketch your big ideas, jot down notes and use Circle to Search with Google¹⁶ all with precision at your fingertips.</li></ul><h4>Sync your world. Simplify your life.</h4><ul><li>Flex more across devices with a connected Galaxy. Send photos and videos in a flash from your Galaxy Galaxy Z Fold6 to other devices with Quick Share.¹⁸ Plus, use your Galaxy Watch¹⁹ as a cam control for your Galaxy foldable to zoom, snap and review photos all without touching your phone.</li></ul>',
        41, 2, false, true, 10, 1);
INSERT INTO product (id, description, gtin, has_options, is_allowed_to_order, is_featured, is_published,
                     is_visible_individually, meta_description, meta_keyword, meta_title, "name", price,
                     short_description, sku, slug, specification, thumbnail_media_id, brand_id, stock_tracking_enabled,
                     tax_included, stock_quantity, tax_class_id)
VALUES (12,
        'The 14-inch MacBook Pro blasts forward with M3 Pro and M3 Max, radically advanced chips that drive even greater performance for more demanding workflows. ',
        'macbook-pro-14-m3-pro', false, true, true, true, true,
        '<p><span style="color: rgb(4, 12, 19);">The 14-inch MacBook Pro blasts forward with M3 Pro and M3 Max, radically advanced chips that drive even greater performance for more demanding workflows.</span></p>',
        '', '', 'MacBook Pro 14 M3 Pro', 1699.0,
        '<p><span style="color: rgb(4, 12, 19);">The 14-inch MacBook Pro blasts forward with M3 Pro and M3 Max, radically advanced chips that drive even greater performance for more demanding workflows.</span></p>',
        'macbook-pro-14-m3-pro', 'macbook-pro-14-m3-pro',
        '<ul><li>SUPERCHARGED BY M3 PRO OR M3 MAX—The Apple M3 Pro chip, with an up to 12-core CPU and up to 18-core GPU using hardware-accelerated ray tracing, delivers amazing performance for demanding workflows like manipulating gigapixel panoramas or compiling millions of lines of code. M3 Max, with an up to 16-core CPU and up to 40-core GPU, drives extreme performance for the most advanced workflows like rendering intricate 3D content or developing transformer models with billions of parameters.</li><li>BUILT FOR APPLE INTELLIGENCE: Apple Intelligence helps you write, express yourself, and get things done effortlessly. It draws on your personal context while setting brand-new standard for privacy in AI. Coming in beta this fall. ⁷</li><li>UP TO 18 HOURS OF BATTERY LIFE¹—Go all day thanks to the power-efficient design of Apple silicon. MacBook Pro delivers the same exceptional performance whether it’s running on battery or plugged in.</li><li>RESPONSIVE UNIFIED MEMORY AND STORAGE—Up to 36GB (M3 Pro) or up to 128GB (M3 Max) of unified memory makes everything you do fast and fluid. Up to 4TB (M3 Pro) or up to 8TB (M3 Max) of superfast SSD storage launches apps and opens files in an instant.²</li><li>BRILLIANT PRO DISPLAY—The 14.2-inch Liquid Retina XDR display³ features Extreme Dynamic Range, 1000 nits of sustained brightness for stunning HDR content, up to 600 nits of brightness for SDR content, and pro reference modes for doing your best work on the go.</li><li>FULLY COMPATIBLE—All your pro apps run lightning fast—including Adobe Creative Cloud, Apple Xcode, Microsoft 365, SideFX Houdini, MathWorks MATLAB, Medivis SurgicalAR, and many of your favorite iPhone and iPad apps.⁴ And with macOS, work and play on your Mac are even more powerful. Elevate your presence on video calls. Access information in all-new ways. And discover even more ways to personalize your Mac.</li><li>ADVANCED CAMERA AND AUDIO—Look sharp and sound great with a 1080p FaceTime HD camera, a studio-quality three-mic array, and a six-speaker sound system with Spatial Audio.</li><li>CONNECT IT ALL—This MacBook Pro features a MagSafe charging port, three Thunderbolt 4 ports, an SDXC card slot, an HDMI port, and a headphone jack. Enjoy fast wireless connectivity with Wi-Fi 6E⁵ and Bluetooth 5.3. And you can connect up to two external displays with M3 Pro, or up to four with M3 Max.</li><li>MAGIC KEYBOARD WITH TOUCH ID—The backlit Magic Keyboard has a full-height function key row and Touch ID, which gives you a fast, easy, secure way to unlock your laptop and sign in to apps and sites.</li><li>ADVANCED SECURITY—Every Mac comes with encryption, robust virus protections, and a powerful firewall system. And free security updates help keep your Mac protected.</li><li>WORKS WITH ALL YOUR APPLE DEVICES—You can do amazing things when you use your Apple devices together. Copy something on iPhone and paste it on MacBook Pro. Use your MacBook Pro to answer FaceTime calls or send texts with Messages.⁶ And that’s just the beginning.</li><li>BUILT TO LAST—The all-aluminum unibody enclosure is exceptionally durable and comes in Space Black and Silver. Free software updates keep things running smoothly for years to come.</li></ul>',
        54, 1, false, true, 10, 1);
INSERT INTO product (id, description, gtin, has_options, is_allowed_to_order, is_featured, is_published,
                     is_visible_individually, meta_description, meta_keyword, meta_title, "name", price,
                     short_description, sku, slug, specification, thumbnail_media_id, brand_id, stock_tracking_enabled,
                     tax_included, stock_quantity, tax_class_id)
VALUES (13, 'Supercharged by the next-generation M2 chip, the redesigned MacBook Air', 'macbook-air-m2', false, true,
        true, true, true,
        '<h3><span style="color: rgb(4, 12, 19);">Supercharged by the next-generation M2 chip, the redesigned MacBook Air</span></h3>',
        '', '', 'MacBook Air M2', 729.0,
        '<h3><span style="color: rgb(4, 12, 19);">Supercharged by the next-generation M2 chip, the redesigned MacBook Air</span></h3>',
        'macbook-air-m2', 'macbook-air-m2',
        '<ul><li>M2 chip for incredible performance</li><li>8-core CPU and up to 10-core GPU to power through complex tasks</li><li>BUILT FOR APPLE INTELLIGENCE: Apple Intelligence helps you write, express yourself, and get things done effortlessly. It draws on your personal context while setting brand-new standard for privacy in AI. Coming in beta this fall.</li><li>16-core Neural Engine for advanced machine learning tasks</li><li>Up to 24GB of unified memory makes everything you do fast and fluid</li><li>Up to 20 percent faster for applying image filters and effects</li><li>Up to 40 percent faster for editing complex video timelines</li><li>Go all day with up to 18 hours of battery life</li><li>Fanless design for silent operation</li><li>13.6-inch Liquid Retina display with 500 nits of brightness and P3 wide color for vibrant images and incredible detail</li><li>1080p FaceTime HD camera</li><li>Three-microphone array focuses on your voice instead of what’s going on around you</li><li>Four-speaker sound system with Spatial Audio for an immersive listening experience</li><li>MagSafe 3 port, two Thunderbolt ports, and headphone jack</li><li>MagSafe charging port, two Thunderbolt ports, and headphone jack</li><li>Backlit Magic Keyboard and Touch ID for secure unlock and payments</li><li>Fast Wi-Fi 6 wireless connectivity</li><li>Superfast SSD storage launches apps and opens files in an instant</li><li>macOS Ventura gives you powerful ways to get more done, share, and collaborate—and works great with iPhone and iPad</li><li>Available in midnight, starlight, space gray, and silver</li></ul>',
        55, 1, false, true, 10, 1);
INSERT INTO product (id, description, gtin, has_options, is_allowed_to_order, is_featured, is_published,
                     is_visible_individually, meta_description, meta_keyword, meta_title, "name", price,
                     short_description, sku, slug, specification, thumbnail_media_id, brand_id, stock_tracking_enabled,
                     tax_included, stock_quantity, tax_class_id)
VALUES (14, 'The M3 chip brings even greater capabilities to the superportable 15-inch MacBook Air', 'macbook-air-m3',
        false, true, true, true, true,
        '<h2><strong style="color: rgb(4, 12, 19);">The M3 chip brings even greater capabilities to the superportable 15-inch MacBook Air</strong></h2>',
        '', '', 'MacBook Air M3', 1299.0,
        '<h2><strong style="color: rgb(4, 12, 19);">The M3 chip brings even greater capabilities to the superportable 15-inch MacBook Air</strong></h2>',
        'macbook-air-m3', 'macbook-air-m3',
        '<ul><li>LEAN. MEAN. M3 MACHINE—The blazing-fast MacBook Air with the M3 chip is a superportable laptop that sails through work and play.</li><li>BUILT FOR APPLE INTELLIGENCE: Apple Intelligence helps you write, express yourself, and get things done effortlessly. It draws on your personal context while setting brand-new standard for privacy in AI. Coming in beta this fall.</li><li>PORTABLE DESIGN—Lightweight and under half an inch thin, so you can take MacBook Air anywhere you go.</li><li>GET MORE DONE FASTER—The powerful 8-core CPU and 10-core GPU of the Apple M3 chip keep things running smoothly.¹</li><li>UP TO 18 HOURS OF BATTERY LIFE—Amazing, all-day battery life so you can leave the power adapter at home.²</li><li>MORE ROOM FOR WHAT YOU LOVE—The 15.3-inch Liquid Retina display supports one billion colors.³</li><li>LOOK SHARP, SOUND GREAT—Everything looks and sounds amazing with a 1080p FaceTime HD camera, three mics, and six speakers with Spatial Audio.</li><li>APPS FLY WITH APPLE SILICON—All your ⁷favorites, from Microsoft 365 to Adobe Creative Cloud, run lightning fast in macOS.⁴</li><li>GET CONNECTED—MacBook Air features two Thunderbolt ports, a headphone jack, Wi-Fi 6E⁵, Bluetooth 5.3, and a MagSafe charging port. And connect up to two external displays with the laptop lid closed.</li><li>IF YOU LOVE IPHONE, YOU’LL LOVE MAC—MacBook Air works like magic with your other Apple devices.⁶ Start an email on your iPhone and finish it on your Mac. Send text messages from your Mac. And much more.</li></ul>',
        59, 1, false, true, 10, 1);
INSERT INTO product (id, description, gtin, has_options, is_allowed_to_order, is_featured, is_published,
                     is_visible_individually, meta_description, meta_keyword, meta_title, "name", price,
                     short_description, sku, slug, specification, thumbnail_media_id, brand_id, stock_tracking_enabled,
                     tax_included, stock_quantity, tax_class_id)
VALUES (15,
        'Unlock your creativity with the tiny but mighty Galaxy Z Flip6, the Galaxy AI powered pocket perfect foldable built for those who want to make a big statement',
        'Samsung Galaxy Z Flip6 ', false, true, true, true, true,
        '<p><span style="color: rgb(4, 12, 19);">Unlock your creativity with the tiny but mighty Galaxy Z Flip6, the Galaxy AI powered pocket perfect foldable built for those who want to make a big statement</span></p>',
        '', '', 'Samsung Galaxy Z Flip6 ', 1099.0,
        '<p><span style="color: rgb(4, 12, 19);">Unlock your creativity with the tiny but mighty Galaxy Z Flip6, the Galaxy AI powered pocket perfect foldable built for those who want to make a big statement</span></p>',
        'Samsung Galaxy Z Flip6 ', 'samsung-galaxy-z-flip6',
        '<h4>Perfectly framed pics, powered by Galaxy AI</h4><ul><li>Capture stunning hands-free photos from any angle using FlexCam with Galaxy AI¹ as it frames your shot automatically. Whether you’re posing in front of a famous landmark or want to get the whole group in, FlexCam² will keep everything you want in frame and zoom in for a gorgeously clear photo.</li></ul><h4>You take the pic. Galaxy AI does the rest.</h4><ul><li>Unleash your creativity without the baggage. Galaxy Z Flip6 takes great photos and easily fixes imperfections, moves objects and enhances colors using Photo Assist with Galaxy AI.¹ ³</li></ul><h4>Flex a new way to capture video</h4><ul><li>Capture social content in a fun new way with Camcorder Mode. When held in FlexMode, Galaxy Z Flip6 lets you easily control the cam with one hand and use Smooth Zoom control to record moments as they happen.</li></ul><h4>Your tiny, mighty translator</h4><ul><li>Say bye to being lost in translation and hi to fluency in up to 16 languages using Interpreter with Galaxy AI. While in FlexMode, your phone displays translations on both sides of the screen for easy in-person conversation.¹ ⁴</li></ul><h4>AI-powered assistance, open or closed</h4><ul><li>Complete simple tasks and send texts without opening your phone on FlexWindow with Galaxy AI. Get quick reply text suggestions based on the context of your conversation, use Bixby to change songs hands-free and more.¹</li></ul><h4>Personalized from the outside in</h4><ul><li>Personalize your cover screen to fit your evolving style with wallpapers that change based on time and weather. No matter the moment, there’s always something new to see on your Galaxy Z Flip6.¹</li></ul><h4>Galaxy AI power, perfect for pockets</h4><ul><li>Big innovation now fits in your pocket or purse with Galaxy Z Flip6. All you need to stay connected and capture gorgeous shots can easily fold to fit into whatever you wear or carry.</li></ul><h4>Strikingly slim. Refreshingly light.</h4><ul><li>Enjoy on-the-go convenience with Galaxy Z Flip6 —the small phone that makes a big statement. No matter the pocket, purse or bag, its light and slim design fits perfectly into your adventurous lifestyle.</li></ul><h4>Tiny, but tough</h4><ul><li>It’s tiny, but tough. Galaxy Z Flip6 is built to last with Gorilla® Glass Victus® 2, IP48 water resistance⁹ and a pre-installed screen protector so you can flip worry-free.</li></ul><h4>A cover screen that has you covered</h4><ul><li>Get more done with your phone closed. Galaxy Z Flip6 lets you quickly reply to messages, get directions and easily check your favorite apps, all from the convenience of your cover screen.</li></ul><h4>Love your health? Put a ring on it.</h4><ul><li>Take control of your health by marrying Galaxy Z Flip6 with Galaxy Ring. Designed for those on the go, Galaxy Ring lets you comfortably keep tabs on your health with sleep tracking, Energy Score and Wellness Tips —all easily viewable on the FlexWindow of your Galaxy Z Flip6.⁶</li></ul><h4>Phone to ear —translations you can hear</h4><ul><li>Get lost in conversation, not translation, when you pair Galaxy Z Flip6 with Buds3 Pro to hear live translations in your ear. Using Interpreter with Galaxy AI, you can hear what the person in front of you is saying in your language, even while they speak another. Plus, with the epic sound quality of Buds3 Pro,⁸ their voice translations sound amazing.⁷</li></ul><h4>Two devices teaming up for your health</h4><ul><li>Track your health on the go with your Galaxy Watch Ultra and get deeper insights on your Galaxy Z Flip6. By pairing the two, get personalized health tracking from your wrist, then see results and actionable goals conveniently on the FlexWindow of your Galaxy Z Flip6.⁵</li></ul><h4>Sync your world. Simplify your life.</h4><ul><li>Flex more across devices with a connected Galaxy. Send photos and videos in a flash from your Galaxy Galaxy Z Flip6 to other devices with Quick Share.¹⁰ Plus, use your Galaxy Watch¹¹ as a cam control for your Galaxy foldable to zoom, snap and review photos all without touching</li></ul><h4>A digital wallet is already in your Galaxy</h4><ul><li>Samsung Wallet is the digital wallet that’s made for your Galaxy Z Flip6 and Watch.¹² Carry your essentials, including your payment cards,¹³ digital ID,¹⁴ keys,¹⁵ movie tickets¹⁶ and more, conveniently in a single app. Make payments, open doors and gain access by simply tapping your phone.</li></ul><p><br></p>',
        64, 2, false, true, 10, 1);
INSERT INTO product (id, description, gtin, has_options, is_allowed_to_order, is_featured, is_published,
                     is_visible_individually, meta_description, meta_keyword, meta_title, "name", price,
                     short_description, sku, slug, specification, thumbnail_media_id, brand_id, stock_tracking_enabled,
                     tax_included, stock_quantity, tax_class_id)
VALUES (16,
        'Intel Core Ultra processors deliver the next level in an immersive graphics experience, and high-performance low power processing',
        'dell-inspiron-16-2-in-1-touch-laptop-intel-core-ultra-7-processor-16gb-memory-1tb-ssd-intel-arc-graphics-ice-blue',
        false, true, true, true, true,
        '<p><span style="color: rgb(4, 12, 19);">Intel&nbsp;Core Ultra processors&nbsp;deliver the next level in an immersive graphics experience, and high-performance low power processing</span></p>',
        '', '',
        'Dell - Inspiron 16” 2-in-1 Touch Laptop - Intel Core Ultra 7 Processor - 16GB Memory - 1TB SSD – Intel Arc Graphics - Ice Blue',
        584.0,
        '<p><span style="color: rgb(4, 12, 19);">Intel&nbsp;Core Ultra processors&nbsp;deliver the next level in an immersive graphics experience, and high-performance low power processing</span></p>',
        'dell-inspiron-16-2-in-1-touch-laptop-intel-core-ultra-7-processor-16gb-memory-1tb-ssd-intel-arc-graphics-ice-blue',
        'dell-inspiron-16-2-in-1-touch-laptop-intel-core-ultra-7-processor-16gb-memory-1tb-ssd-intel-arc-graphics-ice-blue',
        '<h4>AI Ready for Creation and Entertainment</h4><ul><li>With Intel Core Ultra Processors, integrated AI and up to built-in Intel Arc Graphics , you''re ready for anything - from heavy multitasking to creative projects to a studio like entertainment experience</li></ul><h4>Switch between four modes</h4><ul><li>Adapt to any situation by swiftly switching from tablet to tent, stand or laptop mode with the 360° hinge. An optional active pen allows for a natural writing experience</li></ul><h4>Performance in any mode</h4><ul><li>Shift from creating to consuming in any mode with Intel Core Ultra Processors &amp; built-in AI on a spacious 16-inch display.</li></ul><h4>Enhanced Video Chatting with AI</h4><ul><li>Enjoy enhanced videos with an FHD webcam, while the dual microphones and AI noise reduction solution allow you to be heard clearly. New AI-enabled auto framing moves with you, and AI-enabled eye-contact keeps you engaged, even if you glance away</li></ul><h4>Thermals that keep it cool</h4><ul><li>Redesigned with a large heat pipe and fan to keep your system running cool and quiet</li></ul><p><br></p>',
        71, 3, false, true, 10, 1);
INSERT INTO product (id, description, gtin, has_options, is_allowed_to_order, is_featured, is_published,
                     is_visible_individually, meta_description, meta_keyword, meta_title, "name", price,
                     short_description, sku, slug, specification, thumbnail_media_id, brand_id, stock_tracking_enabled,
                     tax_included, stock_quantity, tax_class_id)
VALUES (17,
        'Colorfully reimagined and more versatile than ever. With an all-screen design,10.9-inch Liquid Retina display',
        'ipad-109-inch-10th-generation', false, true, true, true, true,
        '<p><span style="color: rgb(4, 12, 19);">Colorfully reimagined and more versatile than ever. With an all-screen design,10.9-inch Liquid Retina display</span></p>',
        '', '', 'iPad 10.9-Inch (10th Generation)', 349.0,
        '<p><span style="color: rgb(4, 12, 19);">Colorfully reimagined and more versatile than ever. With an all-screen design,10.9-inch Liquid Retina display</span></p>',
        'ipad-109-inch-10th-generation', 'ipad-109-inch-10th-generation',
        '<ul><li>Striking 10.9-inch Liquid Retina display¹ with True Tone</li><li>A14 Bionic chip with 6-core CPU and 4-core GPU</li><li>12MP Wide back camera</li><li>Landscape 12MP Ultra Wide front camera with Center Stage</li><li>Touch ID for secure authentication and Apple Pay</li><li>Stay connected with ultrafast Wi-Fi 6</li><li>USB-C connector for charging and accessories</li><li>Go far with all-day battery life²</li><li>Works with Apple Pencil (USB-C), Apple Pencil (1st generation)³ , and Magic Keyboard Folio⁴</li><li>iPadOS 17 makes your iPad even more capable with powerful new productivity and collaboration features</li><li>¹The display has rounded corners. When measured diagonally as a rectangle, the iPad 10.9-inch screen is 10.9 inches. Actual viewable area is less.</li><li>²Battery life varies by use and configuration. See apple.com/batteries for more information.</li><li>³USB-C to Apple Pencil Adapter required to work with iPad (10th generation). Subject to availability.</li><li>⁴Accessories sold separately and subject to availability. Compatibility varies by generation. Apps are available on the App Store. Title availability is subject to change. Third-party software sold separately.</li></ul>',
        76, 1, false, true, NULL, 1);
ALTER SEQUENCE product_id_seq RESTART WITH 18;

INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (1, 'M3 Pro chip', 12, 1);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (2, '14-core GPU', 12, 2);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (3, 'Apple M3', 14, 1);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (4, 'Apple M3 10-core', 14, 2);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (5, '8 GB', 14, 10);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (6, '512GB', 14, 11);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (7, '15.3 inches', 14, 4);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (8, 'macOS Sonoma', 14, 3);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (9, 'Snapdragon® 8 Gen 3 for Galaxy 3.4 gigahertz', 15, 1);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (10, 'Snapdragon® 8 GPU', 15, 2);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (11, ' 12 GB', 15, 10);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (12, '256GB', 15, 11);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (13, '6.7 inches', 15, 4);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (14, 'Android 14', 15, 3);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (15, '2640 x 1080', 15, 12);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (16, 'Intel Core Ultra 7 Series 1', 16, 1);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (17, 'Intel Arc', 16, 2);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (18, '16 gigabytes', 16, 10);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (19, '1000 gigabytes', 16, 11);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (20, '16 inches', 16, 4);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (21, 'Win 11 Pro', 16, 3);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (22, '1920 x 1200 (Full HD+)', 16, 12);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (23, 'A14 Bionic chip', 17, 1);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (24, 'A14 Bionic chip', 17, 2);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (25, '4GB', 17, 10);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (26, '64 gigabytes', 17, 11);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (27, '10.9 inches', 17, 4);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (28, 'Apple iPadOS', 17, 3);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (29, '2360 x 1640', 17, 12);
INSERT INTO product_attribute_value (id, value, product_id, product_attribute_id)
VALUES (30, 'Liquid Retina display', 17, 5);
ALTER SEQUENCE product_attribute_value_id_seq RESTART WITH 31;

INSERT INTO category (id, "name", slug, parent_id, is_published, image_id)
VALUES (1, 'Phone', 'phone', NULL, true, 1);
INSERT INTO category (id, "name", slug, parent_id, is_published, image_id)
VALUES (2, 'Tablet', 'tablet', NULL, true, 2);
INSERT INTO category (id, "name", slug, parent_id, is_published, image_id)
VALUES (3, 'Galaxy', 'galaxy', 1, true, 3);
INSERT INTO category (id, "name", slug, parent_id, is_published, image_id)
VALUES (4, 'iPhone', 'iphone', 1, true, 4);
INSERT INTO category (id, "name", slug, parent_id, is_published, image_id)
VALUES (5, 'Laptop', 'laptop', NULL, true, 5);
INSERT INTO category (id, "name", slug, parent_id, is_published, image_id)
VALUES (6, 'Workstation', 'workstation', 5, true, 6);
INSERT INTO category (id, "name", slug, parent_id, is_published, image_id)
VALUES (7, 'iPad', 'ipad', 2, true, 35);
INSERT INTO category (id, "name", slug, parent_id, is_published, image_id)
VALUES (8, 'Galaxy Tab', 'galaxy-tab', 2, true, 36);
INSERT INTO category (id, "name", slug, parent_id, is_published, image_id)
VALUES (9, 'Macbook', 'macbook', 5, true, 48);
ALTER SEQUENCE category_id_seq RESTART WITH 10;

INSERT INTO product_category (id, display_order, is_featured_product, category_id, product_id)
VALUES (1, 0, false, 1, 1);
INSERT INTO product_category (id, display_order, is_featured_product, category_id, product_id)
VALUES (2, 0, false, 4, 1);
INSERT INTO product_category (id, display_order, is_featured_product, category_id, product_id)
VALUES (3, 0, false, 1, 2);
INSERT INTO product_category (id, display_order, is_featured_product, category_id, product_id)
VALUES (4, 0, false, 4, 2);
INSERT INTO product_category (id, display_order, is_featured_product, category_id, product_id)
VALUES (5, 0, false, 1, 3);
INSERT INTO product_category (id, display_order, is_featured_product, category_id, product_id)
VALUES (6, 0, false, 4, 3);
INSERT INTO product_category (id, display_order, is_featured_product, category_id, product_id)
VALUES (7, 0, false, 1, 4);
INSERT INTO product_category (id, display_order, is_featured_product, category_id, product_id)
VALUES (8, 0, false, 4, 4);
INSERT INTO product_category (id, display_order, is_featured_product, category_id, product_id)
VALUES (9, 0, false, 5, 5);
INSERT INTO product_category (id, display_order, is_featured_product, category_id, product_id)
VALUES (10, 0, false, 6, 5);
INSERT INTO product_category (id, display_order, is_featured_product, category_id, product_id)
VALUES (14, 0, false, 1, 8);
INSERT INTO product_category (id, display_order, is_featured_product, category_id, product_id)
VALUES (15, 0, false, 3, 8);
INSERT INTO product_category (id, display_order, is_featured_product, category_id, product_id)
VALUES (16, 0, false, 5, 12);
INSERT INTO product_category (id, display_order, is_featured_product, category_id, product_id)
VALUES (17, 0, false, 9, 12);
INSERT INTO product_category (id, display_order, is_featured_product, category_id, product_id)
VALUES (18, 0, false, 5, 13);
INSERT INTO product_category (id, display_order, is_featured_product, category_id, product_id)
VALUES (19, 0, false, 9, 13);
INSERT INTO product_category (id, display_order, is_featured_product, category_id, product_id)
VALUES (20, 0, false, 5, 14);
INSERT INTO product_category (id, display_order, is_featured_product, category_id, product_id)
VALUES (21, 0, false, 9, 14);
INSERT INTO product_category (id, display_order, is_featured_product, category_id, product_id)
VALUES (22, 0, false, 1, 15);
INSERT INTO product_category (id, display_order, is_featured_product, category_id, product_id)
VALUES (23, 0, false, 3, 15);
INSERT INTO product_category (id, display_order, is_featured_product, category_id, product_id)
VALUES (24, 0, false, 5, 16);
INSERT INTO product_category (id, display_order, is_featured_product, category_id, product_id)
VALUES (25, 0, false, 6, 16);
INSERT INTO product_category (id, display_order, is_featured_product, category_id, product_id)
VALUES (28, 0, false, 2, 17);
INSERT INTO product_category (id, display_order, is_featured_product, category_id, product_id)
VALUES (29, 0, false, 7, 17);
ALTER SEQUENCE product_category_id_seq RESTART WITH 30;

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
VALUES (11, 3, 1);
INSERT INTO product_related (id, product_id, related_product_id)
VALUES (12, 3, 2);
INSERT INTO product_related (id, product_id, related_product_id)
VALUES (13, 4, 1);
INSERT INTO product_related (id, product_id, related_product_id)
VALUES (14, 4, 3);
INSERT INTO product_related (id, product_id, related_product_id)
VALUES (15, 4, 2);
INSERT INTO product_related (id, product_id, related_product_id)
VALUES (16, 7, 6);
INSERT INTO product_related (id, product_id, related_product_id)
VALUES (17, 8, 1);
INSERT INTO product_related (id, product_id, related_product_id)
VALUES (18, 8, 3);
INSERT INTO product_related (id, product_id, related_product_id)
VALUES (19, 8, 4);
INSERT INTO product_related (id, product_id, related_product_id)
VALUES (20, 8, 2);
INSERT INTO product_related (id, product_id, related_product_id)
VALUES (21, 12, 5);
INSERT INTO product_related (id, product_id, related_product_id)
VALUES (22, 13, 6);
INSERT INTO product_related (id, product_id, related_product_id)
VALUES (23, 13, 12);
INSERT INTO product_related (id, product_id, related_product_id)
VALUES (24, 14, 12);
INSERT INTO product_related (id, product_id, related_product_id)
VALUES (25, 14, 13);
INSERT INTO product_related (id, product_id, related_product_id)
VALUES (26, 15, 1);
INSERT INTO product_related (id, product_id, related_product_id)
VALUES (27, 15, 3);
INSERT INTO product_related (id, product_id, related_product_id)
VALUES (28, 15, 4);
INSERT INTO product_related (id, product_id, related_product_id)
VALUES (29, 15, 2);
INSERT INTO product_related (id, product_id, related_product_id)
VALUES (30, 15, 8);
INSERT INTO product_related (id, product_id, related_product_id)
VALUES (31, 16, 5);
INSERT INTO product_related (id, product_id, related_product_id)
VALUES (32, 16, 12);
INSERT INTO product_related (id, product_id, related_product_id)
VALUES (33, 16, 13);
INSERT INTO product_related (id, product_id, related_product_id)
VALUES (34, 16, 14);
INSERT INTO product_related (id, product_id, related_product_id)
VALUES (35, 17, 7);
INSERT INTO product_related (id, product_id, related_product_id)
VALUES (36, 17, 6);
ALTER SEQUENCE product_related_id_seq RESTART WITH 37;