package com.levi.demo.application.usecase.command;

import com.levi.demo.domain.enums.ParkingEventType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ParkingEventCommand(
        String licensePlate,
        LocalDateTime entryTime,
        LocalDateTime exitTime,
        BigDecimal lat,
        BigDecimal lng,
        ParkingEventType eventType
) {}
