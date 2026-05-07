package com.levi.demo.infrastructure.gateway;

import com.levi.demo.domain.enums.ParkingSessionStatus;
import com.levi.demo.domain.model.ParkingSession;
import com.levi.demo.domain.model.Sector;
import com.levi.demo.domain.model.Spot;
import com.levi.demo.infrastructure.repository.ParkingSessionJpaRepository;
import com.levi.demo.infrastructure.repository.SectorJpaRepository;
import com.levi.demo.infrastructure.repository.SpotJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ParkingSessionGatewayIT {

    @Autowired
    private ParkingSessionGateway parkingSessionGateway;

    @Autowired
    private ParkingSessionJpaRepository parkingSessionJpaRepository;

    @Autowired
    private SectorJpaRepository sectorJpaRepository;

    @Autowired
    private SpotJpaRepository spotJpaRepository;

    @Test
    void shouldSaveAndFindActiveSessionByLicensePlate() {
        ParkingSession session = ParkingSession.start(
                "ABC1234",
                LocalDateTime.of(2026, 5, 5, 10, 0)
        );

        parkingSessionGateway.save(session);

        Optional<ParkingSession> found =
                parkingSessionGateway.findActiveByLicensePlate("ABC1234");

        assertTrue(found.isPresent());
        assertEquals("ABC1234", found.get().getLicensePlate());
        assertEquals(ParkingSessionStatus.ENTRY, found.get().getStatus());
    }

    @Test
    void shouldReturnEmptyWhenThereIsNoActiveSession() {
        ParkingSession session = ParkingSession.start(
                "ABC1234",
                LocalDateTime.of(2026, 5, 5, 10, 0)
        );

        Sector sector = sectorJpaRepository.save(
                new Sector("A", new BigDecimal("40.50"), 10)
        );

        Spot spot = spotJpaRepository.save(new Spot(
                1L,
                sector,
                new BigDecimal("-23.561684"),
                new BigDecimal("-46.655981")
        ));

        session.park(spot, new BigDecimal("1.00"));
        session.finish(
                LocalDateTime.of(2026, 5, 5, 11, 0),
                new BigDecimal("40.50")
        );

        parkingSessionJpaRepository.save(session);

        Optional<ParkingSession> found =
                parkingSessionGateway.findActiveByLicensePlate("ABC1234");

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldSumRevenueBySectorAndDate() {
        Sector sector = sectorJpaRepository.save(
                new Sector("A", new BigDecimal("40.50"), 10)
        );

        Spot spot = spotJpaRepository.save(new Spot(
                1L,
                sector,
                new BigDecimal("-23.561684"),
                new BigDecimal("-46.655981")
        ));

        ParkingSession session = ParkingSession.start(
                "ABC1234",
                LocalDateTime.of(2026, 5, 5, 10, 0)
        );

        session.park(spot, new BigDecimal("1.00"));
        session.finish(
                LocalDateTime.of(2026, 5, 5, 11, 0),
                new BigDecimal("40.50")
        );

        parkingSessionGateway.save(session);

        BigDecimal revenue = parkingSessionGateway.sumRevenueBySectorAndDate(
                "A",
                LocalDate.of(2026, 5, 5)
        );

        assertEquals(new BigDecimal("40.50"), revenue);
    }

    @Test
    void shouldReturnZeroWhenThereIsNoRevenue() {
        BigDecimal revenue = parkingSessionGateway.sumRevenueBySectorAndDate(
                "A",
                LocalDate.of(2026, 5, 5)
        );

        assertEquals(new BigDecimal("0"), revenue);
    }
}