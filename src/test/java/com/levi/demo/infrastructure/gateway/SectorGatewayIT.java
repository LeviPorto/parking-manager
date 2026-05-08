package com.levi.demo.infrastructure.gateway;

import com.levi.demo.domain.model.Sector;
import com.levi.demo.infrastructure.repository.SectorJpaRepository;
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
class SectorGatewayIT {

    @Autowired
    private SectorGateway sectorGateway;

    @Autowired
    private SectorJpaRepository sectorJpaRepository;

    @Test
    void shouldSaveAndFindSectorByName() {
        Sector sector = new Sector("A", new BigDecimal("40.50"), 10);

        Sector saved = sectorGateway.save(sector);

        Optional<Sector> found = sectorGateway.findByName("A");

        assertTrue(found.isPresent());
        assertNotNull(saved.getId());
        assertEquals("A", found.get().getName());
        assertEquals(new BigDecimal("40.50"), found.get().getBasePrice());
        assertEquals(10, found.get().getMaxCapacity());
    }
}