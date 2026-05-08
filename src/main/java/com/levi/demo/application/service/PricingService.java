package com.levi.demo.application.service;

import com.levi.demo.domain.model.ParkingSession;
import com.levi.demo.domain.model.Sector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Slf4j
public class PricingService {

    private static final BigDecimal LOW_OCCUPANCY_THRESHOLD = new BigDecimal("0.25");
    private static final BigDecimal MEDIUM_OCCUPANCY_THRESHOLD = new BigDecimal("0.50");
    private static final BigDecimal HIGH_OCCUPANCY_THRESHOLD = new BigDecimal("0.75");

    private static final BigDecimal LOW_OCCUPANCY_MULTIPLIER = new BigDecimal("0.90");
    private static final BigDecimal NORMAL_OCCUPANCY_MULTIPLIER = new BigDecimal("1.00");
    private static final BigDecimal MEDIUM_OCCUPANCY_MULTIPLIER = new BigDecimal("1.10");
    private static final BigDecimal HIGH_OCCUPANCY_MULTIPLIER = new BigDecimal("1.25");

    private static final int FREE_PARKING_TOLERANCE_MINUTES = 30;

    public BigDecimal calculateOccupancyMultiplier(Sector sector) {
        BigDecimal occupancyRate = sector.getOccupancyRate();
        log.info("Calculating occupancy rate {} for sector {}",
                occupancyRate, sector.getName());

        if (occupancyRate.compareTo(LOW_OCCUPANCY_THRESHOLD) < 0) {
            return LOW_OCCUPANCY_MULTIPLIER;
        }

        if (occupancyRate.compareTo(MEDIUM_OCCUPANCY_THRESHOLD) <= 0) {
            return NORMAL_OCCUPANCY_MULTIPLIER;
        }

        if (occupancyRate.compareTo(HIGH_OCCUPANCY_THRESHOLD) <= 0) {
            return MEDIUM_OCCUPANCY_MULTIPLIER;
        }

        return HIGH_OCCUPANCY_MULTIPLIER;
    }

    public BigDecimal calculateOccupancyAmount(ParkingSession session, LocalDateTime exitTime) {
        long minutes = Duration.between(session.getEntryTime(), exitTime).toMinutes();

        /*if (minutes <= FREE_PARKING_TOLERANCE_MINUTES) {
            log.info("Parking session {} was free", session.getId());
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }*/

        long hours = (long) Math.ceil(minutes / 60.0);

        BigDecimal entryBasePrice =  session.getEntryBasePrice();
        BigDecimal occupancyMultiplier = session.getOccupancyMultiplier();
        BigDecimal bigDecimalHours =  BigDecimal.valueOf(hours);

        log.info("Session entry base price: {}, occupancy multiplier: {}, "
                + " and hours: {}", entryBasePrice, occupancyMultiplier, bigDecimalHours);

        return entryBasePrice
                .multiply(occupancyMultiplier)
                .multiply(bigDecimalHours)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
