package com.levi.demo.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.levi.demo.domain.enums.ParkingEventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ParkingEventRequest(

        @JsonProperty("license_plate")
        @NotBlank
        String licensePlate,

        @JsonProperty("event_type")
        @NotNull
        ParkingEventType eventType,

        @JsonProperty("entry_time")
        LocalDateTime entryTime,

        @JsonProperty("exit_time")
        LocalDateTime exitTime,

        BigDecimal lat,
        BigDecimal lng
) {}
