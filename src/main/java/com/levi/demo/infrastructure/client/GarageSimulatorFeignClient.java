package com.levi.demo.infrastructure.client;

import com.levi.demo.application.port.out.client.GarageSimulatorClient;
import com.levi.demo.infrastructure.client.dto.GarageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "garageSimulatorClient", url = "${garage.simulator.url}")
public interface GarageSimulatorFeignClient extends GarageSimulatorClient {

    @Override
    @GetMapping("/garage")
    GarageResponse getGarage();
}
