CREATE TABLE sectors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    base_price DECIMAL(10,2) NOT NULL,
    max_capacity INT NOT NULL,
    occupied_count INT NOT NULL DEFAULT 0,
    closed BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT chk_sectors_base_price_positive
        CHECK (base_price >= 0),

    CONSTRAINT chk_sectors_max_capacity_positive
        CHECK (max_capacity > 0),

    CONSTRAINT chk_sectors_occupied_count_valid
        CHECK (
            occupied_count >= 0
            AND occupied_count <= max_capacity
        )
);

CREATE TABLE spots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    external_id BIGINT NOT NULL UNIQUE,
    sector_id BIGINT NOT NULL,
    lat DECIMAL(10,7) NOT NULL,
    lng DECIMAL(10,7) NOT NULL,
    occupied BOOLEAN NOT NULL DEFAULT FALSE,
    current_license_plate VARCHAR(255),

    CONSTRAINT fk_spots_sector
        FOREIGN KEY (sector_id)
        REFERENCES sectors(id),

    CONSTRAINT chk_spots_lat_valid
        CHECK (lat BETWEEN -90 AND 90),

    CONSTRAINT chk_spots_lng_valid
        CHECK (lng BETWEEN -180 AND 180)
);

CREATE TABLE parking_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    license_plate VARCHAR(255) NOT NULL,
    sector_id BIGINT,
    spot_id BIGINT,
    entry_time DATETIME NOT NULL,
    exit_time DATETIME,
    entry_base_price DECIMAL(10,2),
    occupancy_multiplier DECIMAL(5,2),
    final_amount DECIMAL(10,2) DEFAULT 0.00,
    status ENUM('ENTRY', 'PARKED', 'EXITED') NOT NULL,

    CONSTRAINT fk_parking_sessions_sector
        FOREIGN KEY (sector_id)
        REFERENCES sectors(id),

    CONSTRAINT fk_parking_sessions_spot
        FOREIGN KEY (spot_id)
        REFERENCES spots(id),

    CONSTRAINT chk_parking_sessions_exit_after_entry
        CHECK (
            exit_time IS NULL
            OR exit_time >= entry_time
        ),

    CONSTRAINT chk_parking_sessions_final_amount_positive
        CHECK (final_amount >= 0),

    CONSTRAINT chk_parking_sessions_base_price_positive
        CHECK (
            entry_base_price IS NULL
            OR entry_base_price >= 0
        ),

    CONSTRAINT chk_parking_sessions_multiplier_positive
        CHECK (
            occupancy_multiplier IS NULL
            OR occupancy_multiplier > 0
        )
);

CREATE INDEX idx_spots_sector_id
    ON spots(sector_id);