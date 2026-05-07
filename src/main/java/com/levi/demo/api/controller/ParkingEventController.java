package com.levi.demo.api.controller;

import com.levi.demo.api.dto.ParkingEventRequest;
import com.levi.demo.api.mapper.ParkingEventMapper;
import com.levi.demo.application.port.in.ProcessParkingEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ParkingEventController {

    private final ProcessParkingEvent processParkingEvent;
    private final ParkingEventMapper mapper;

    public ParkingEventController(ProcessParkingEvent processParkingEvent,
                                  ParkingEventMapper mapper) {
        this.processParkingEvent = processParkingEvent;
        this.mapper = mapper;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> process(@RequestBody ParkingEventRequest request) {
        log.info("Parking event received: {}", request);
        processParkingEvent.execute(mapper.toCommand(request));
        log.info("Parking event processed: {}", request);
        return ResponseEntity.ok().build();
    }

}
