package com.levi.demo.application.exception;

public class SectorNotFoundException extends ApplicationException {
    public SectorNotFoundException(Long spotId) {
        super("Sector '%s' not found for spot id".formatted(spotId));
    }
}
