-- Bicycle parts for Mart
INSERT INTO parts (name, description, type, location, quantity, condition, notes, created_at, updated_at) VALUES
('Carbon Road Frame', 'Lightweight carbon fiber frame, 56cm', 'FRAME', 'Garage - Wall rack', 1, 'EXCELLENT', 'From old race bike', NOW(), NOW()),
('Aluminum MTB Frame', '26 inch mountain bike frame', 'FRAME', 'Garage - Floor', 1, 'GOOD', 'Needs new paint', NOW(), NOW()),
('Steel Touring Frame', 'Classic Reynolds 531 steel', 'FRAME', 'Basement', 1, 'FAIR', 'Vintage, some rust spots', NOW(), NOW()),
('Shimano Ultegra Brake Set', 'Road bike caliper brakes', 'BRAKE', 'Garage - Shelf A', 2, 'NEW', 'Still in box', NOW(), NOW()),
('Avid BB7 Disc Brakes', 'Mechanical disc brakes', 'BRAKE', 'Garage - Shelf A', 1, 'GOOD', 'Front brake only', NOW(), NOW()),
('Vintage Weinmann Brakes', 'Side-pull brakes', 'BRAKE', 'Basement - Box 3', 4, 'FAIR', 'Mixed condition', NOW(), NOW()),
('Continental GP5000', '700x25c road tires', 'TIRE', 'Garage - Tire rack', 3, 'NEW', 'Bought on sale', NOW(), NOW()),
('Schwalbe Marathon', '700x32c touring tires', 'TIRE', 'Garage - Tire rack', 2, 'EXCELLENT', 'Puncture resistant', NOW(), NOW()),
('Maxxis Minion DHF', '27.5x2.5 MTB tire', 'TIRE', 'Garage - Floor', 1, 'GOOD', 'Some wear on knobs', NOW(), NOW()),
('Topeak Joe Blow', 'Floor pump with gauge', 'PUMP', 'Garage - Corner', 1, 'EXCELLENT', 'Main pump', NOW(), NOW()),
('Lezyne Mini Pump', 'Portable hand pump', 'PUMP', 'Bike bag', 1, 'GOOD', 'Always carry this', NOW(), NOW()),
('Old Floor Pump', 'No brand, works fine', 'PUMP', 'Basement', 1, 'FAIR', 'Gauge broken', NOW(), NOW()),
('Shimano 105 Rear Derailleur', '11-speed', 'OTHER', 'Garage - Shelf B', 1, 'EXCELLENT', 'Backup derailleur', NOW(), NOW()),
('Chain Collection', 'Various 10/11 speed chains', 'OTHER', 'Garage - Drawer', 5, 'NEW', 'KMC and Shimano', NOW(), NOW()),
('Handlebar Tape', 'Cork tape, various colors', 'OTHER', 'Garage - Drawer', 8, 'NEW', 'Black, white, red', NOW(), NOW());

-- Vinyl records for Katrin
INSERT INTO vinyl_records (title, artist, release_year, genre, purchase_source, purchase_date, condition, notes, created_at, updated_at) VALUES
('Abbey Road', 'The Beatles', 1969, 'ROCK', 'Tallinn flea market', '2020-06-15', 'VERY_GOOD', 'Found this gem on a rainy Saturday. The medley on side B is perfect.', NOW(), NOW()),
('Dark Side of the Moon', 'Pink Floyd', 1973, 'ROCK', 'Discogs', '2021-03-08', 'EXCELLENT', 'Original UK pressing! Spent too much but worth it.', NOW(), NOW()),
('Rumours', 'Fleetwood Mac', 1977, 'ROCK', 'Local record store', '2019-11-22', 'GOOD', 'Some surface noise but plays well', NOW(), NOW()),
('Led Zeppelin IV', 'Led Zeppelin', 1971, 'ROCK', 'Inheritance from uncle', '2018-01-01', 'FAIR', 'Sentimental value, played a lot', NOW(), NOW()),
('Kind of Blue', 'Miles Davis', 1959, 'JAZZ', 'Jazz cafe closing sale', '2022-08-30', 'MINT', 'Sealed reissue, haven''t opened yet', NOW(), NOW()),
('A Love Supreme', 'John Coltrane', 1965, 'JAZZ', 'Online auction', '2021-12-05', 'EXCELLENT', 'Spiritual experience every listen', NOW(), NOW()),
('Time Out', 'Dave Brubeck Quartet', 1959, 'JAZZ', 'Antique shop', '2020-04-18', 'VERY_GOOD', 'Take Five never gets old', NOW(), NOW()),
('King of the Delta Blues Singers', 'Robert Johnson', 1961, 'BLUES', 'Record fair', '2023-05-12', 'GOOD', 'Historic recordings, essential blues', NOW(), NOW()),
('Blues Breakers', 'John Mayall with Eric Clapton', 1966, 'BLUES', 'Gift from friend', '2019-07-20', 'VERY_GOOD', 'Clapton''s early work', NOW(), NOW()),
('Selected Ambient Works 85-92', 'Aphex Twin', 1992, 'ELECTRONIC', 'Discogs', '2022-02-14', 'MINT', 'Perfect late night music', NOW(), NOW()),
('Homework', 'Daft Punk', 1997, 'ELECTRONIC', 'Record store day', '2023-04-22', 'EXCELLENT', 'Da Funk on vinyl hits different', NOW(), NOW()),
('Goldberg Variations', 'Glenn Gould', 1956, 'CLASSICAL', 'Estate sale', '2021-09-03', 'GOOD', 'The legendary 1955 recording', NOW(), NOW()),
('The Four Seasons', 'Vivaldi / I Musici', 1959, 'CLASSICAL', 'Mother''s collection', '2017-12-25', 'FAIR', 'Christmas gift, childhood memories', NOW(), NOW()),
('What''s Going On', 'Marvin Gaye', 1971, 'SOUL', 'Vinyl cafe', '2022-06-19', 'EXCELLENT', 'Timeless message, beautiful sound', NOW(), NOW()),
('Songs in the Key of Life', 'Stevie Wonder', 1976, 'SOUL', 'Discogs', '2020-10-08', 'VERY_GOOD', 'Double album masterpiece', NOW(), NOW());
