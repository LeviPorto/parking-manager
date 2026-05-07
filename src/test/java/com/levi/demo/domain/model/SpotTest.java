package com.levi.demo.domain.model;

import com.levi.demo.domain.exception.SpotAlreadyOccupiedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SpotTest {

    @Test
    void shouldOccupySpot() {
        Sector sector = new Sector("A", new BigDecimal("10.00"), 10);
        Spot spot = new Spot(1L, sector, new BigDecimal("-23.561684"), new BigDecimal("-46.655981"));

        spot.occupy("ABC1234");

        assertTrue(spot.getOccupied());
        assertEquals("ABC1234", spot.getCurrentLicensePlate());
    }

    @Test
    void shouldThrowExceptionWhenSpotAlreadyOccupied() {
        Sector sector = new Sector("A", new BigDecimal("10.00"), 10);
        Spot spot = new Spot(1L, sector, new BigDecimal("-23.561684"), new BigDecimal("-46.655981"));

        spot.occupy("ABC1234");

        assertThrows(
                SpotAlreadyOccupiedException.class,
                () -> spot.occupy("XYZ9999")
        );
    }

    @Test
    void shouldReleaseSpot() {
        Sector sector = new Sector("A", new BigDecimal("10.00"), 10);
        Spot spot = new Spot(1L, sector, new BigDecimal("-23.561684"), new BigDecimal("-46.655981"));

        spot.occupy("ABC1234");
        spot.release();

        assertFalse(spot.getOccupied());
        assertNull(spot.getCurrentLicensePlate());
    }
}