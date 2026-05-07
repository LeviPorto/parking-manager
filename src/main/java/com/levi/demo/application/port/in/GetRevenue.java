package com.levi.demo.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface GetRevenue {
    BigDecimal retrieve(LocalDate date, String sector);
}
