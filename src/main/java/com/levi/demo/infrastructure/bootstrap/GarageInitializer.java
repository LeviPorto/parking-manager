package com.levi.demo.infrastructure.bootstrap;

import com.levi.demo.application.port.in.InitializeGarage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GarageInitializer implements ApplicationRunner {

    private final InitializeGarage initializeGarage;

    public GarageInitializer(InitializeGarage initializeGarage) {
        this.initializeGarage = initializeGarage;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            initializeGarage.execute();
            log.info("Garage initialized successfully");
        } catch (Exception exception) {
            log.warn("Garage initialization failed. "
                    + "Start the simulator and restart the app if needed. "
                    + "Cause: {}", exception.getMessage());
        }
    }
}
