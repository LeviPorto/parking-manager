package com.levi.demo.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.levi.demo.domain.enums.ParkingEventType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ParkingEventRequest(

        @JsonProperty("license_plate")
        String licensePlate,

        @JsonProperty("event_type")
        ParkingEventType eventType,

        @JsonProperty("entry_time")
        LocalDateTime entryTime,

        @JsonProperty("exit_time")
        LocalDateTime exitTime,

        BigDecimal lat,
        BigDecimal lng
) {}
