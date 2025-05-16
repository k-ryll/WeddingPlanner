-- Create category table
CREATE TABLE IF NOT EXISTS category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Create join table for vendor-category many-to-many relationship
CREATE TABLE IF NOT EXISTS vendor_category (
    vendor_id INT NOT NULL,
    category_id INT NOT NULL,
    PRIMARY KEY (vendor_id, category_id),
    FOREIGN KEY (vendor_id) REFERENCES vendor(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE
);

-- Insert distinct categories from existing vendors
INSERT INTO category (name)
SELECT DISTINCT category FROM vendor WHERE category IS NOT NULL;

-- Link vendors to their categories
INSERT INTO vendor_category (vendor_id, category_id)
SELECT v.id, c.id FROM vendor v
JOIN category c ON v.category = c.name
WHERE v.category IS NOT NULL; 