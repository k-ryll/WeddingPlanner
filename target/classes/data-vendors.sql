-- Vendor table structure (if not already created)
CREATE TABLE IF NOT EXISTS vendor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    location VARCHAR(255),
    price_type VARCHAR(20),      -- 'fixed', 'per_guest', etc.
    price_per_guest DECIMAL(10,2),
    total_price DECIMAL(10,2),
    contact VARCHAR(255),
    description TEXT
);

-- Catering vendors
INSERT INTO vendor (name, category, location, price_type, price_per_guest, total_price, contact, description) VALUES
('Hizon\'s Catering Services Inc', 'Catering', 'Quezon City', 'per_guest', 1200.00, 180000.00, 'http://www.hizonscatering.com', 'Premium catering service with extensive menu options'),
('Josiah\'s Catering', 'Catering', 'Mandaluyong City', 'per_guest', 1500.00, 225000.00, 'https://josiahscatering.com', 'High-end catering with customizable menu packages'),
('Bizu Catering Studio', 'Catering', 'Makati City', 'per_guest', 1800.00, 270000.00, 'https://bizucatering.com', 'French-inspired gourmet food'),
('The Plaza', 'Catering', 'Makati City', 'per_guest', 1350.00, 202500.00, 'https://theplazacatering.com', 'Classic Filipino and international cuisine');

-- Venue vendors
INSERT INTO vendor (name, category, location, price_type, price_per_guest, total_price, contact, description) VALUES
('Shangri-La at the Fort', 'Venue', 'Taguig City', 'fixed', NULL, 350000.00, 'https://www.shangri-la.com/manila/shangrilaatthefort/', 'Luxurious hotel with elegant ballrooms'),
('The Blue Leaf Cosmopolitan', 'Venue', 'Quezon City', 'fixed', NULL, 250000.00, 'https://www.theblueleaf.com.ph', 'Contemporary event spaces with garden features'),
('Sofitel Philippine Plaza Manila', 'Venue', 'Pasay City', 'fixed', NULL, 400000.00, 'https://www.sofitelmanila.com', 'Seaside venue with panoramic views of Manila Bay'),
('The Bellevue Manila', 'Venue', 'Alabang', 'fixed', NULL, 200000.00, 'https://www.thebellevue.com', 'Elegant hotel venue with spacious function rooms');

-- Photography vendors
INSERT INTO vendor (name, category, location, price_type, price_per_guest, total_price, contact, description) VALUES
('Nice Print Photography', 'Photography', 'Metro Manila', 'fixed', NULL, 80000.00, 'https://niceprintphoto.com', 'Full wedding photography coverage with multiple photographers'),
('Pat Dy Photography', 'Photography', 'Metro Manila', 'fixed', NULL, 120000.00, 'https://patdyphotography.com', 'Premium wedding photography with artistic style'),
('Metrophoto', 'Photography', 'Quezon City', 'fixed', NULL, 90000.00, 'https://metro-photo.com', 'Contemporary wedding photography with cinematic approach'),
('Mayad Studios', 'Photography', 'Metro Manila', 'fixed', NULL, 85000.00, 'https://mayadstudios.com', 'Photography and videography with creative storytelling');

-- Videography vendors
INSERT INTO vendor (name, category, location, price_type, price_per_guest, total_price, contact, description) VALUES
('Jason Magbanua', 'Videography', 'Metro Manila', 'fixed', NULL, 150000.00, 'https://jasonmagbanua.com', 'Premium wedding films with emotional storytelling'),
('Cinemaworks', 'Videography', 'Pasig City', 'fixed', NULL, 90000.00, 'https://cinemaworksweddings.com', 'Cinema-quality wedding films'),
('Treehouse Story', 'Videography', 'Mandaluyong City', 'fixed', NULL, 75000.00, 'https://treehousestory.com', 'Documentary-style wedding videos'),
('Notion in Motion', 'Videography', 'Makati City', 'fixed', NULL, 85000.00, 'https://notioninmotion.com', 'Creative wedding videography with cinematic approach');

-- Wedding Planner vendors
INSERT INTO vendor (name, category, location, price_type, price_per_guest, total_price, contact, description) VALUES
('Amanda Events', 'Wedding Planner', 'Makati City', 'fixed', NULL, 120000.00, 'https://amandaevents.ph', 'Full-service wedding planning and coordination'),
('Passion Cooks', 'Wedding Planner', 'Quezon City', 'fixed', NULL, 100000.00, 'https://passioncooks.com', 'Planning, catering, and styling services'),
('Rita Neri Event Planners', 'Wedding Planner', 'Makati City', 'fixed', NULL, 150000.00, 'https://ritanerieventplanners.com', 'Luxury wedding planning with 30+ years experience'),
('Events by EC', 'Wedding Planner', 'Pasig City', 'fixed', NULL, 90000.00, 'https://eventsbyec.com', 'Personalized wedding planning and coordination');

-- Florist vendors
INSERT INTO vendor (name, category, location, price_type, price_per_guest, total_price, contact, description) VALUES
('Gideon Hermosa', 'Flowers', 'Metro Manila', 'fixed', NULL, 200000.00, 'https://gideonhermosa.com', 'Luxury floral arrangements and event styling'),
('Teddy Manuel', 'Flowers', 'Metro Manila', 'fixed', NULL, 180000.00, 'https://teddymanuel.com', 'High-end floral designs and event styling'),
('Vatel Manila', 'Flowers', 'Metro Manila', 'fixed', NULL, 150000.00, 'https://vatelmanila.com', 'Bespoke bridal bouquets and floral arrangements'),
('The Flower Farm', 'Flowers', 'Tagaytay', 'fixed', NULL, 100000.00, 'https://theflowerfarm.ph', 'Fresh flower arrangements with rustic touch');

-- Music/Entertainment vendors
INSERT INTO vendor (name, category, location, price_type, price_per_guest, total_price, contact, description) VALUES
('Manila Philharmonic Orchestra', 'Entertainment', 'Metro Manila', 'fixed', NULL, 80000.00, 'https://mpo.org.ph', 'Classical music ensemble for elegant weddings'),
('Sound Salad', 'Entertainment', 'Metro Manila', 'fixed', NULL, 45000.00, 'https://soundsalad.ph', 'Wedding band with versatile music repertoire'),
('The Brass Munkeys', 'Entertainment', 'Metro Manila', 'fixed', NULL, 60000.00, 'https://thebrassmunkeys.com', 'Popular wedding band with wide song selection'),
('DJ Callum', 'Entertainment', 'Metro Manila', 'fixed', NULL, 30000.00, 'https://djcallum.com', 'Professional DJ services for wedding receptions');

-- Cake vendors
INSERT INTO vendor (name, category, location, price_type, price_per_guest, total_price, contact, description) VALUES
('Cake Concepts', 'Cake', 'Makati City', 'fixed', NULL, 25000.00, 'https://cakeconcepts.ph', 'Custom wedding cakes with elegant designs'),
('Penk Ching', 'Cake', 'Quezon City', 'fixed', NULL, 35000.00, 'https://penkching.com', 'Artistic cake designs from renowned pastry chef'),
('Honey Glaze Cakes', 'Cake', 'Mandaluyong City', 'fixed', NULL, 20000.00, 'https://honeyglazecakes.com', 'Customizable wedding cakes with premium ingredients'),
('Bethany Dream Cakes', 'Cake', 'Pasig City', 'fixed', NULL, 18000.00, 'https://bethanydreamcakes.com', 'Beautiful wedding cakes with personalized designs');

-- Hair and Makeup vendors
INSERT INTO vendor (name, category, location, price_type, price_per_guest, total_price, contact, description) VALUES
('Makeup by Qua', 'Beauty', 'Metro Manila', 'fixed', NULL, 35000.00, 'https://makeupbyqua.com', 'Bridal makeup services for the entire entourage'),
('Juan Sarte', 'Beauty', 'Metro Manila', 'fixed', NULL, 40000.00, 'https://juansarte.com', 'Celebrity makeup artist for bridal services'),
('Stylized Studio', 'Beauty', 'Makati City', 'fixed', NULL, 25000.00, 'https://stylizedstudio.com', 'Hair and makeup services for brides and entourage'),
('Cats Del Rosario', 'Beauty', 'Pasig City', 'fixed', NULL, 30000.00, 'https://catsdelrosario.com', 'Professional makeup artist specializing in bridal looks');

-- Attire vendors
INSERT INTO vendor (name, category, location, price_type, price_per_guest, total_price, contact, description) VALUES
('Francis Libiran', 'Attire', 'Makati City', 'fixed', NULL, 250000.00, 'https://francislibiran.com', 'Luxury wedding gowns with intricate details'),
('Michael Cinco', 'Attire', 'Metro Manila', 'fixed', NULL, 350000.00, 'https://michaelcinco.com', 'Haute couture wedding dresses'),
('Joey Samson', 'Attire', 'Makati City', 'fixed', NULL, 120000.00, 'https://joeysamson.com', 'Custom tailored suits for grooms'),
('Rosa Clara', 'Attire', 'BGC, Taguig', 'fixed', NULL, 180000.00, 'https://rosaclara.com', 'Spanish bridal fashion with modern elegance'); 