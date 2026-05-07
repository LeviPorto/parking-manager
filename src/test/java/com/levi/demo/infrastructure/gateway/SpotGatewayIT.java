package com.levi.demo.infrastructure.gateway;

import com.levi.demo.domain.model.Sector;
import com.levi.demo.domain.model.Spot;
import com.levi.demo.infrastructure.repository.SectorJpaRepository;
import com.levi.demo.infrastructure.repository.SpotJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class SpotGatewayIT {

    @Autowired
    private SpotGateway spotGateway;

    @Autowired
    private SectorJpaRepository sectorJpaRepository;

    @Autowired
    private SpotJpaRepository spotJpaRepository;

    @Test
    void shouldSaveAndFindSpotByExternalId() {
        Sector sector = sectorJpaRepository.save(
                new Sector("A", new BigDecimal("40.50"), 10)
        );

        Spot spot = new Spot(
                1L,
                sector,
                new BigDecimal("-23.561684"),
                new BigDecimal("-46.655981")
        );

        spotGateway.save(spot);

        Optional<Spot> found = spotGateway.findByExternalId(1L);

        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getExternalId());
        assertEquals("A", found.get().getSector().getName());
    }

    @Test
    void shouldFindSpotByCoordinatesForUpdate() {
        Sector sector = sectorJpaRepository.save(
                new Sector("A", new BigDecimal("40.50"), 10)
        );

        Spot spot = spotJpaRepository.save(new Spot(
                2L,
                sector,
                new BigDecimal("-23.561684"),
                new BigDecimal("-46.655981")
        ));

        Optional<Spot> found = spotGateway.findByCoordinatesForUpdate(
                new BigDecimal("-23.561684"),
                new BigDecimal("-46.655981")
        );

        assertTrue(found.isPresent());
        assertEquals(spot.getId(), found.get().getId());
        assertEquals(2L, found.get().getExternalId());
    }

    @Test
    void shouldSaveOccupiedSpot() {
        Sector sector = sectorJpaRepository.save(
                new Sector("A", new BigDecimal("40.50"), 10)
        );

        Spot spot = new Spot(
                3L,
                sector,
                new BigDecimal("-23.561600"),
                new BigDecimal("-46.655900")
        );

        spot.occupy("ABC1234");

        Spot saved = spotGateway.save(spot);

        Optional<Spot> found = spotGateway.findByExternalId(3L);

        assertNotNull(saved.getId());
        assertTrue(found.isPresent());
        assertTrue(found.get().getOccupied());
        assertEquals("ABC1234", found.get().getCurrentLicensePlate());
    }
}