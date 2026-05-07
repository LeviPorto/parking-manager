package com.levi.demo.domain.exception;


public class SpotAlreadyOccupiedException extends DomainException {
    public SpotAlreadyOccupiedException(Long spotId) {
        super("Spot '%s' is already occupied".formatted(spotId));
    }
}
