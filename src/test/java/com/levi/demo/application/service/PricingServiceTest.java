package com.levi.demo.application.service;

import com.levi.demo.domain.model.ParkingSession;
import com.levi.demo.domain.model.Sector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricingServiceTest {

    @InjectMocks
    private PricingService pricingService;

    @Test
    void shouldReturnZeroWhenParkingTimeIsLessThanThirtyMinutes() {
        ParkingSession session = mock(ParkingSession.class);

        when(session.getEntryTime()).thenReturn(LocalDateTime.of(2026, 5, 5, 10, 0));

        BigDecimal amount = pricingService.calculateOccupancyAmount(
                session,
                LocalDateTime.of(2026, 5, 5, 10, 20)
        );

        assertEquals(new BigDecimal("0.00"), amount);
    }

    @Test
    void shouldChargeOneHourWhenParkingTimeIsBetweenThirtyAndSixtyMinutes() {
        ParkingSession session = mock(ParkingSession.class);

        when(session.getEntryTime()).thenReturn(LocalDateTime.of(2026, 5, 5, 10, 0));
        when(session.getEntryBasePrice()).thenReturn(new BigDecimal("10.00"));
        when(session.getOccupancyMultiplier()).thenReturn(new BigDecimal("1.10"));

        BigDecimal amount = pricingService.calculateOccupancyAmount(
                session,
                LocalDateTime.of(2026, 5, 5, 10, 45)
        );

        assertEquals(new BigDecimal("11.00"), amount);
    }

    @Test
    void shouldChargeTwoHoursWhenParkingTimeIsMoreThanOneHour() {
        ParkingSession session = mock(ParkingSession.class);

        when(session.getEntryTime()).thenReturn(LocalDateTime.of(2026, 5, 5, 10, 0));
        when(session.getEntryBasePrice()).thenReturn(new BigDecimal("10.00"));
        when(session.getOccupancyMultiplier()).thenReturn(new BigDecimal("1.25"));

        BigDecimal amount = pricingService.calculateOccupancyAmount(
                session,
                LocalDateTime.of(2026, 5, 5, 11, 1)
        );

        assertEquals(new BigDecimal("25.00"), amount);
    }

    @Test
    void shouldReturnLowOccupancyMultiplier() {
        Sector sector = mock(Sector.class);
        when(sector.getOccupancyRate()).thenReturn(new BigDecimal("0.10"));

        assertEquals(new BigDecimal("0.90"), pricingService.calculateOccupancyMultiplier(sector));
    }

    @Test
    void shouldReturnNormalOccupancyMultiplier() {
        Sector sector = mock(Sector.class);
        when(sector.getOccupancyRate()).thenReturn(new BigDecimal("0.40"));

        assertEquals(new BigDecimal("1.00"), pricingService.calculateOccupancyMultiplier(sector));
    }

    @Test
    void shouldReturnMediumOccupancyMultiplier() {
        Sector sector = mock(Sector.class);
        when(sector.getOccupancyRate()).thenReturn(new BigDecimal("0.70"));

        assertEquals(new BigDecimal("1.10"), pricingService.calculateOccupancyMultiplier(sector));
    }

    @Test
    void shouldReturnHighOccupancyMultiplier() {
        Sector sector = mock(Sector.class);
        when(sector.getOccupancyRate()).thenReturn(new BigDecimal("0.90"));

        assertEquals(new BigDecimal("1.25"), pricingService.calculateOccupancyMultiplier(sector));
    }

    @Test
    void shouldReturnNormalMultiplierWhenOccupancyIsExactly25Percent() {
        Sector sector = mock(Sector.class);
        when(sector.getOccupancyRate()).thenReturn(new BigDecimal("0.25"));

        assertEquals(new BigDecimal("1.00"), pricingService.calculateOccupancyMultiplier(sector));
    }

    @Test
    void shouldReturnNormalMultiplierWhenOccupancyIsExactly50Percent() {
        Sector sector = mock(Sector.class);
        when(sector.getOccupancyRate()).thenReturn(new BigDecimal("0.50"));

        assertEquals(new BigDecimal("1.00"), pricingService.calculateOccupancyMultiplier(sector));
    }

    @Test
    void shouldReturnIncreasedMultiplierWhenOccupancyIsExactly75Percent() {
        Sector sector = mock(Sector.class);
        when(sector.getOccupancyRate()).thenReturn(new BigDecimal("0.75"));

        assertEquals(new BigDecimal("1.10"), pricingService.calculateOccupancyMultiplier(sector));
    }

    @Test
    void shouldReturnIncreasedMultiplierWhenOccupancyIsJustAbove50Percent() {
        Sector sector = mock(Sector.class);
        when(sector.getOccupancyRate()).thenReturn(new BigDecimal("0.5001"));

        assertEquals(new BigDecimal("1.10"), pricingService.calculateOccupancyMultiplier(sector));
    }

    @Test
    void shouldReturnHighMultiplierWhenOccupancyIsJustAbove75Percent() {
        Sector sector = mock(Sector.class);
        when(sector.getOccupancyRate()).thenReturn(new BigDecimal("0.7501"));

        assertEquals(new BigDecimal("1.25"), pricingService.calculateOccupancyMultiplier(sector));
    }
}