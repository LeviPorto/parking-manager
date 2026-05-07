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

        BigDecimal multiplier = pricingService.calculateOccupancyMultiplier(sector);

        assertEquals(new BigDecimal("0.90"), multiplier);
    }

    @Test
    void shouldReturnNormalOccupancyMultiplier() {
        Sector sector = mock(Sector.class);
        when(sector.getOccupancyRate()).thenReturn(new BigDecimal("0.40"));

        BigDecimal multiplier = pricingService.calculateOccupancyMultiplier(sector);

        assertEquals(new BigDecimal("1.00"), multiplier);
    }

    @Test
    void shouldReturnMediumOccupancyMultiplier() {
        Sector sector = mock(Sector.class);
        when(sector.getOccupancyRate()).thenReturn(new BigDecimal("0.70"));

        BigDecimal multiplier = pricingService.calculateOccupancyMultiplier(sector);

        assertEquals(new BigDecimal("1.10"), multiplier);
    }

    @Test
    void shouldReturnHighOccupancyMultiplier() {
        Sector sector = mock(Sector.class);
        when(sector.getOccupancyRate()).thenReturn(new BigDecimal("0.90"));

        BigDecimal multiplier = pricingService.calculateOccupancyMultiplier(sector);

        assertEquals(new BigDecimal("1.25"), multiplier);
    }
}