package com.levi.demo.application.exception;

import java.math.BigDecimal;

public class SpotNotFoundException extends ApplicationException {
    public SpotNotFoundException(BigDecimal lat, BigDecimal lng) {
        super("Spot not found for coordinates lat=%s, lng=%s".formatted(lat, lng));
    }
}
