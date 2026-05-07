package com.levi.demo.application.usecase;

import com.levi.demo.application.port.out.client.GarageSimulatorClient;
import com.levi.demo.application.port.out.repository.SectorRepository;
import com.levi.demo.application.port.out.repository.SpotRepository;
import com.levi.demo.domain.model.Sector;
import com.levi.demo.domain.model.Spot;
import com.levi.demo.infrastructure.client.dto.GarageDto;
import com.levi.demo.infrastructure.client.dto.GarageResponse;
import com.levi.demo.infrastructure.client.dto.SpotDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitializeGarageImplTest {

    @Mock
    private GarageSimulatorClient garageSimulatorClient;

    @Mock
    private SectorRepository sectorRepository;

    @Mock
    private SpotRepository spotRepository;

    @InjectMocks
    private InitializeGarageImpl initializeGarage;

    @Test
    void shouldInitializeGarageSuccessfully() {
        GarageResponse response = new GarageResponse(
                List.of(new GarageDto("A", new BigDecimal("40.50"), 10)),
                List.of(new SpotDto(
                        1L,
                        "A",
                        new BigDecimal("-23.561684"),
                        new BigDecimal("-46.655981"),
                        false
                ))
        );

        Sector sector = new Sector("A", new BigDecimal("40.50"), 10);

        when(garageSimulatorClient.getGarage()).thenReturn(response);

        when(sectorRepository.findByName("A"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(sector));

        when(sectorRepository.save(any(Sector.class)))
                .thenReturn(sector);

        when(spotRepository.findByExternalId(1L))
                .thenReturn(Optional.empty());

        initializeGarage.execute();

        verify(sectorRepository).save(any(Sector.class));
        verify(spotRepository).save(any(Spot.class));
    }

    @Test
    void shouldNotCreateSectorWhenSectorAlreadyExists() {
        Sector sector = new Sector("A", new BigDecimal("40.50"), 10);

        GarageResponse response = new GarageResponse(
                List.of(new GarageDto("A", new BigDecimal("40.50"), 10)),
                List.of()
        );

        when(garageSimulatorClient.getGarage()).thenReturn(response);
        when(sectorRepository.findByName("A")).thenReturn(Optional.of(sector));

        initializeGarage.execute();

        verify(sectorRepository, never()).save(any(Sector.class));
    }

    @Test
    void shouldNotCreateSpotWhenSpotAlreadyExists() {
        Sector sector = new Sector("A", new BigDecimal("40.50"), 10);

        Spot spot = new Spot(
                1L,
                sector,
                new BigDecimal("-23.561684"),
                new BigDecimal("-46.655981")
        );

        GarageResponse response = new GarageResponse(
                List.of(),
                List.of(new SpotDto(
                        1L,
                        "A",
                        new BigDecimal("-23.561684"),
                        new BigDecimal("-46.655981"),
                        false
                ))
        );

        when(garageSimulatorClient.getGarage()).thenReturn(response);
        when(spotRepository.findByExternalId(1L)).thenReturn(Optional.of(spot));

        initializeGarage.execute();

        verify(spotRepository, never()).save(any(Spot.class));
        verify(sectorRepository, never()).findByName(anyString());
    }
}