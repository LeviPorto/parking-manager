package com.levi.demo.application.port.out.client;

import com.levi.demo.infrastructure.client.dto.GarageResponse;

public interface GarageSimulatorClient {
    GarageResponse getGarage();
}
