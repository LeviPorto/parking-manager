package com.levi.demo.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record RevenueRequest(
        @JsonProperty("date") LocalDate date,
        @JsonProperty("sector") String sector
) {}
