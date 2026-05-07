package com.levi.demo.infrastructure.client.dto;

import java.util.List;

public record GarageResponse(
        List<GarageDto> garage,
        List<SpotDto> spots
) {}
