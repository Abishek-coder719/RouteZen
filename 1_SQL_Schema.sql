-- Route Zen Database Schema
-- MySQL Database for Bus Scheduling System

CREATE DATABASE IF NOT EXISTS routezen_db;
USE routezen_db;

-- Buses Table
CREATE TABLE buses (
    bus_id INT PRIMARY KEY AUTO_INCREMENT,
    bus_number VARCHAR(20) UNIQUE NOT NULL,
    bus_type ENUM('ORDINARY', 'LIMITED', 'SUPERFAST') NOT NULL,
    operator VARCHAR(50) NOT NULL,
    total_seats INT NOT NULL,
    wifi_available BOOLEAN DEFAULT FALSE,
    ac_available BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Stops Table
CREATE TABLE stops (
    stop_id INT PRIMARY KEY AUTO_INCREMENT,
    stop_name VARCHAR(100) UNIQUE NOT NULL,
    distance_from_origin INT NOT NULL COMMENT 'Distance in kilometers',
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    INDEX idx_stop_name (stop_name)
);

-- Schedules Table
CREATE TABLE schedules (
    schedule_id INT PRIMARY KEY AUTO_INCREMENT,
    bus_id INT NOT NULL,
    from_stop_id INT NOT NULL,
    to_stop_id INT NOT NULL,
    departure_time TIME NOT NULL,
    arrival_time TIME NOT NULL,
    duration_minutes INT NOT NULL,
    fare DECIMAL(6,2) NOT NULL,
    available_seats INT NOT NULL,
    schedule_date DATE NOT NULL,
    FOREIGN KEY (bus_id) REFERENCES buses(bus_id) ON DELETE CASCADE,
    FOREIGN KEY (from_stop_id) REFERENCES stops(stop_id),
    FOREIGN KEY (to_stop_id) REFERENCES stops(stop_id),
    INDEX idx_schedule_date (schedule_date),
    INDEX idx_stops (from_stop_id, to_stop_id)
);

-- Live Tracking Table
CREATE TABLE live_tracking (
    tracking_id INT PRIMARY KEY AUTO_INCREMENT,
    schedule_id INT NOT NULL,
    current_stop_id INT NOT NULL,
    estimated_arrival_minutes INT NOT NULL,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (schedule_id) REFERENCES schedules(schedule_id) ON DELETE CASCADE,
    FOREIGN KEY (current_stop_id) REFERENCES stops(stop_id)
);

-- Sample Data Insertion
INSERT INTO stops (stop_name, distance_from_origin) VALUES
('Kuttipuram', 0),
('Thangal Padi', 3),
('Kavumpuram', 6),
('Kandanakam', 9),
('Mannor', 12),
('Edappal', 15),
('Naduvattom', 18),
('Kalachal', 21),
('Changramkullam', 30),
('Valayamkullam', 33),
('Kunamkullam', 57),
('Choondal', 59),
('Kechery', 60),
('Thrissur', 75);

INSERT INTO buses (bus_number, bus_type, operator, total_seats, wifi_available, ac_available) VALUES
('ORD-1234', 'ORDINARY', 'KSRTC', 45, FALSE, FALSE),
('ORD-5678', 'ORDINARY', 'PRIVATE', 45, FALSE, FALSE),
('LIM-2345', 'LIMITED', 'KSRTC', 40, TRUE, FALSE),
('LIM-6789', 'LIMITED', 'PRIVATE', 40, TRUE, FALSE),
('SUP-3456', 'SUPERFAST', 'KSRTC', 35, TRUE, TRUE),
('SUP-7890', 'SUPERFAST', 'PRIVATE', 35, TRUE, TRUE);
