package com.levi.demo.application.port.out.repository;

import com.levi.demo.domain.model.ParkingSession;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface ParkingSessionRepository {
    Optional<ParkingSession> findActiveByLicensePlate(String licensePlate);
    ParkingSession save(ParkingSession session);
    BigDecimal sumRevenueBySectorAndDate(String sector, LocalDate date);
}
