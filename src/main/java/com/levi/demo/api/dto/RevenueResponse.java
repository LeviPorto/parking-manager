package com.levi.demo.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RevenueResponse(
        BigDecimal amount,
        String currency,
        LocalDateTime timestamp
) {}
