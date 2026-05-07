package com.levi.demo.infrastructure.client.dto;

import java.math.BigDecimal;

public record SpotDto(
        Long id,
        String sector,
        BigDecimal lat,
        BigDecimal lng,
        Boolean occupied
) {}
