package com.levi.demo.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record GarageDto(
        String sector,

        @JsonProperty("base_price")
        BigDecimal basePrice,

        @JsonProperty("max_capacity")
        Integer maxCapacity
) {}
