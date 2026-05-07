package com.levi.demo.infrastructure.gateway;

import com.levi.demo.application.port.out.repository.ParkingSessionRepository;
import com.levi.demo.domain.model.ParkingSession;
import com.levi.demo.infrastructure.repository.ParkingSessionJpaRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Component
public class ParkingSessionGateway implements ParkingSessionRepository {

    private final ParkingSessionJpaRepository repository;

    public ParkingSessionGateway(ParkingSessionJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<ParkingSession> findActiveByLicensePlate(String licensePlate) {
        return repository.findActiveByLicensePlate(licensePlate);
    }

    @Override
    public ParkingSession save(ParkingSession session) {
        return repository.save(session);
    }

    @Override
    public BigDecimal sumRevenueBySectorAndDate(String sector, LocalDate date) {
        return repository.sumRevenueBySectorAndDate(sector, date);
    }
}
