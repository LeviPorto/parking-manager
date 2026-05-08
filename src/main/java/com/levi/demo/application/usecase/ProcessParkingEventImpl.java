package com.levi.demo.application.usecase;

import com.levi.demo.application.usecase.command.ParkingEventCommand;
import com.levi.demo.application.port.in.ProcessParkingEvent;
import com.levi.demo.application.port.out.repository.ParkingSessionRepository;
import com.levi.demo.application.port.out.repository.SectorRepository;
import com.levi.demo.application.port.out.repository.SpotRepository;
import com.levi.demo.application.service.PricingService;
import com.levi.demo.application.exception.ActiveParkingSessionNotFoundException;
import com.levi.demo.application.exception.SectorNotFoundException;
import com.levi.demo.application.exception.SpotNotFoundException;
import com.levi.demo.domain.model.ParkingSession;
import com.levi.demo.domain.model.Sector;
import com.levi.demo.domain.model.Spot;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class ProcessParkingEventImpl implements ProcessParkingEvent {

    private final ParkingSessionRepository parkingSessionRepository;
    private final SpotRepository spotRepository;
    private final SectorRepository sectorRepository;
    private final PricingService pricingService;

    public ProcessParkingEventImpl(ParkingSessionRepository parkingSessionRepository, SpotRepository spotRepository,
                                   SectorRepository sectorRepository, PricingService pricingService
    ) {
        this.parkingSessionRepository = parkingSessionRepository;
        this.spotRepository = spotRepository;
        this.sectorRepository = sectorRepository;
        this.pricingService = pricingService;
    }

    @Override
    @Transactional
    public void execute(ParkingEventCommand command) {
        switch (command.eventType()) {
            case ENTRY -> handleEntry(command);
            case PARKED -> handleParked(command);
            case EXIT -> handleExit(command);
        }
    }

    private void handleEntry(ParkingEventCommand command) {
        log.info("Handling entry for license plate {}", command.licensePlate());

        if (parkingSessionRepository.findActiveByLicensePlate(command.licensePlate()).isPresent()) {
            log.warn("Duplicated entry for the same license plate {}",
                    command.licensePlate());
            return;
        }
        parkingSessionRepository.save(ParkingSession.start(command.licensePlate(),
                command.entryTime()));
    }

    private void handleParked(ParkingEventCommand command) {
        log.info("Handling parking for license plate {}", command.licensePlate());

        ParkingSession session = parkingSessionRepository.findActiveByLicensePlate(command.licensePlate())
                .orElseThrow(() -> new ActiveParkingSessionNotFoundException(command.licensePlate()));

        Spot spot = spotRepository.findByCoordinatesForUpdate(command.lat(), command.lng())
                .orElseThrow(() -> new SpotNotFoundException(command.lat(), command.lng()));

        Sector sector = spot.getSector();

        if (sector == null) {
            throw new SectorNotFoundException(spot.getId());
        }

        BigDecimal occupancyMultiplier = pricingService.calculateOccupancyMultiplier(sector);

        log.info("Calculated occupancy multiplier {} for sector {}",
                occupancyMultiplier, sector.getName());

        spot.occupy(command.licensePlate());
        sector.reserveSpot();
        session.park(spot, occupancyMultiplier);

        sectorRepository.save(sector);
        spotRepository.save(spot);
        parkingSessionRepository.save(session);
    }

    private void handleExit(ParkingEventCommand command) {
        log.info("Handling exiting for license plate {}", command.licensePlate());

        ParkingSession session = parkingSessionRepository.findActiveByLicensePlate(command.licensePlate())
                .orElseThrow(() -> new ActiveParkingSessionNotFoundException(command.licensePlate()));

        BigDecimal amount = pricingService
                .calculateOccupancyAmount(session, command.exitTime());
        log.info("Final amount {} was calculated for license plate {}",
                amount, command.licensePlate());

        if (session.getSpot() != null) {
            Spot spot = spotRepository.findByIdForUpdate(session.getSpot().getId())
                    .orElseThrow(() -> new SpotNotFoundException(session.getSpot().getId()));

            spot.release();
            spotRepository.save(spot);
        }
        if (session.getSector() != null) {
            session.getSector().releaseSpot();
            sectorRepository.save(session.getSector());
        }

        session.finish(command.exitTime(), amount);

        parkingSessionRepository.save(session);
    }

}
