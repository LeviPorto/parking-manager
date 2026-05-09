package com.levi.demo.domain.model;

import com.levi.demo.domain.exception.SectorFullException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SectorTest {

    @Test
    void shouldReturnFalseWhenSectorIsNotFull() {
        Sector sector = new Sector("A", new BigDecimal("10.00"), 2);

        assertFalse(sector.isFull());
    }

    @Test
    void shouldReturnTrueWhenSectorIsFull() {
        Sector sector = new Sector("A", new BigDecimal("10.00"), 1);

        sector.reserveSpot();

        assertTrue(sector.isFull());
    }

    @Test
    void shouldCalculateOccupancyRate() {
        Sector sector = new Sector("A", new BigDecimal("10.00"), 4);

        sector.reserveSpot();

        assertEquals(new BigDecimal("0.2500"), sector.getOccupancyRate());
    }

    @Test
    void shouldReturnZeroOccupancyRateWhenCapacityIsZero() {
        Sector sector = new Sector("A", new BigDecimal("10.00"), 0);

        assertEquals(BigDecimal.ZERO, sector.getOccupancyRate());
    }

    @Test
    void shouldReserveSpotSuccessfully() {
        Sector sector = new Sector("A", new BigDecimal("10.00"), 2);

        sector.reserveSpot();

        assertEquals(1, sector.getOccupiedCount());
    }

    @Test
    void shouldThrowExceptionWhenSectorIsFull() {
        Sector sector = new Sector("A", new BigDecimal("10.00"), 1);

        sector.reserveSpot();

        assertThrows(SectorFullException.class, sector::reserveSpot);
    }

    @Test
    void shouldReleaseSpot() {
        Sector sector = new Sector("A", new BigDecimal("10.00"), 2);

        sector.reserveSpot();
        sector.releaseSpot();

        assertEquals(0, sector.getOccupiedCount());
    }
}