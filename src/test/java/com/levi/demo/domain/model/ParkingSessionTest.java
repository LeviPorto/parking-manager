package com.levi.demo.domain.model;

import com.levi.demo.domain.enums.ParkingSessionStatus;
import com.levi.demo.domain.exception.InvalidExitStateException;
import com.levi.demo.domain.exception.InvalidParkingStateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ParkingSessionTest {

    @Test
    void shouldStartSession() {
        ParkingSession session = ParkingSession.start(
                "ABC1234",
                LocalDateTime.of(2026, 5, 5, 10, 0)
        );

        assertEquals("ABC1234", session.getLicensePlate());
        assertEquals(ParkingSessionStatus.ENTRY, session.getStatus());
        assertEquals(LocalDateTime.of(2026, 5, 5, 10, 0), session.getEntryTime());
    }

    @Test
    void shouldParkSuccessfully() {
        Sector sector = new Sector("A", new BigDecimal("10.00"), 10);
        Spot spot = new Spot(1L, sector, new BigDecimal("-23.561684"), new BigDecimal("-46.655981"));

        ParkingSession session = ParkingSession.start(
                "ABC1234",
                LocalDateTime.of(2026, 5, 5, 10, 0)
        );

        session.park(
                spot,
                new BigDecimal("1.10"));

        assertEquals(ParkingSessionStatus.PARKED, session.getStatus());
        assertEquals(spot, session.getSpot());
        assertEquals(sector, session.getSector());
        assertEquals(new BigDecimal("10.00"), session.getEntryBasePrice());
        assertEquals(new BigDecimal("1.10"), session.getOccupancyMultiplier());
    }

    @Test
    void shouldFinishSuccessfully() {
        Sector sector = new Sector("A", new BigDecimal("10.00"), 10);
        Spot spot = new Spot(1L, sector, new BigDecimal("-23.561684"), new BigDecimal("-46.655981"));

        ParkingSession session = ParkingSession.start(
                "ABC1234",
                LocalDateTime.of(2026, 5, 5, 10, 0)
        );

        session.park(
                spot,
                new BigDecimal("1.00")
        );

        session.finish(
                LocalDateTime.of(2026, 5, 5, 11, 0),
                new BigDecimal("10.00")
        );

        assertEquals(ParkingSessionStatus.EXITED, session.getStatus());
        assertEquals(new BigDecimal("10.00"), session.getFinalAmount());
    }

    @Test
    void shouldThrowExceptionWhenParkingSessionIsNotInEntryStatus() {
        Sector sector = new Sector("A", new BigDecimal("40.50"), 10);

        Spot spot = new Spot(
                1L,
                sector,
                new BigDecimal("-23.561684"),
                new BigDecimal("-46.655981")
        );

        ParkingSession session = ParkingSession.start(
                "ABC1234",
                LocalDateTime.of(2026, 5, 5, 10, 0)
        );

        session.park(spot, new BigDecimal("1.00"));

        assertThrows(
                InvalidParkingStateException.class,
                () -> session.park(spot, new BigDecimal("1.00"))
        );
    }

    @Test
    void shouldThrowExceptionWhenFinishingSessionThatIsNotParked() {
        ParkingSession session = ParkingSession.start(
                "ABC1234",
                LocalDateTime.of(2026, 5, 5, 10, 0)
        );

        assertThrows(
                InvalidExitStateException.class,
                () -> session.finish(
                        LocalDateTime.of(2026, 5, 5, 11, 0),
                        new BigDecimal("40.50")
                )
        );
    }
}