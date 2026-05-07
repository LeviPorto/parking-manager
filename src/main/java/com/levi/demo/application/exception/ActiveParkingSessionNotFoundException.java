package com.levi.demo.application.exception;

public class ActiveParkingSessionNotFoundException extends ApplicationException {
    public ActiveParkingSessionNotFoundException(String licensePlate) {
        super("Active parking session not found for license plate '%s'"
                .formatted(licensePlate));
    }
}
