package com.levi.demo.api.controller;

import com.levi.demo.api.dto.RevenueRequest;
import com.levi.demo.api.dto.RevenueResponse;
import com.levi.demo.api.mapper.RevenueMapper;
import com.levi.demo.application.port.in.GetRevenue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@Slf4j
public class RevenueController {

    private final GetRevenue getRevenue;
    private final RevenueMapper mapper;

    public RevenueController(GetRevenue getRevenue, RevenueMapper mapper) {
        this.getRevenue = getRevenue;
        this.mapper = mapper;
    }

    @GetMapping("/revenue")
    public RevenueResponse getRevenue(@RequestBody RevenueRequest request) {
        return mapper
                .toResponse(getRevenue.retrieve(request.date(), request.sector()));
    }
}
