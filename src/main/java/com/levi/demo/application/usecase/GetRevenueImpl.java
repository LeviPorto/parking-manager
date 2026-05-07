package com.levi.demo.application.usecase;

import com.levi.demo.application.port.in.GetRevenue;
import com.levi.demo.application.port.out.repository.ParkingSessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Slf4j
public class GetRevenueImpl implements GetRevenue {

    private final ParkingSessionRepository parkingSessionRepository;

    public GetRevenueImpl(ParkingSessionRepository parkingSessionRepository) {
        this.parkingSessionRepository = parkingSessionRepository;
    }

    @Override
    public BigDecimal retrieve(LocalDate date, String sector) {
        log.info("Retrieving revenue for sector {} and date {}",
                sector, date);
        return parkingSessionRepository.sumRevenueBySectorAndDate(sector, date);
    }
}
