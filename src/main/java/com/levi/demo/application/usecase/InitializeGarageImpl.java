package com.levi.demo.application.usecase;

import com.levi.demo.application.port.in.InitializeGarage;
import com.levi.demo.application.port.out.client.GarageSimulatorClient;
import com.levi.demo.application.port.out.repository.SectorRepository;
import com.levi.demo.application.port.out.repository.SpotRepository;
import com.levi.demo.domain.model.Sector;
import com.levi.demo.domain.model.Spot;
import com.levi.demo.infrastructure.client.dto.GarageDto;
import com.levi.demo.infrastructure.client.dto.GarageResponse;
import com.levi.demo.infrastructure.client.dto.SpotDto;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class InitializeGarageImpl implements InitializeGarage {

    private final GarageSimulatorClient garageSimulatorClient;
    private final SectorRepository sectorRepository;
    private final SpotRepository spotRepository;

    public InitializeGarageImpl(GarageSimulatorClient garageSimulatorClient, SectorRepository sectorRepository,
                                SpotRepository spotRepository) {
        this.garageSimulatorClient = garageSimulatorClient;
        this.sectorRepository = sectorRepository;
        this.spotRepository = spotRepository;
    }

    @Override
    @Transactional
    public void execute() {
        log.info("Executing garage initialization");
        GarageResponse response = garageSimulatorClient.getGarage();

        for (GarageDto dto : response.garage()) {
            Optional<Sector> optionalSector = sectorRepository.findByName(dto.sector());

            boolean sectorAlreadyExists = optionalSector.isPresent();

            if (sectorAlreadyExists) {
                log.warn("Sector {} already exists, skipping it",
                        optionalSector.get().getId());
                continue;
            }

            Sector sector = new Sector(
                    dto.sector(),
                    dto.basePrice(),
                    dto.maxCapacity()
            );

            sectorRepository.save(sector);
        }

        for (SpotDto dto : response.spots()) {

            Optional<Spot> optionalSpot = spotRepository.findByExternalId(dto.id());

            boolean spotAlreadyExists = optionalSpot.isPresent();

            if (spotAlreadyExists) {
                log.warn("Spot {} already exists, skipping it",
                        optionalSpot.get().getId());
                continue;
            }

            Sector sector = sectorRepository.findByName(dto.sector())
                    .orElseThrow();

            Spot spot = new Spot(
                    dto.id(),
                    sector,
                    dto.lat(),
                    dto.lng()
            );

            spotRepository.save(spot);
        }
    }
}
