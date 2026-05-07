package com.levi.demo.api.controller;

import com.levi.demo.api.exception.GlobalExceptionHandler;
import com.levi.demo.api.mapper.ParkingEventMapper;
import com.levi.demo.application.port.in.ProcessParkingEvent;
import com.levi.demo.application.usecase.command.ParkingEventCommand;
import com.levi.demo.domain.enums.ParkingEventType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParkingEventController.class)
@Import(GlobalExceptionHandler.class)
class ParkingEventControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParkingEventMapper mapper;

    @MockBean
    private ProcessParkingEvent processParkingEvent;

    @Test
    void shouldReturn200WhenEventIsValid() throws Exception {
        when(mapper.toCommand(any()))
                .thenReturn(new ParkingEventCommand(
                        "ABC1234",
                        LocalDateTime.of(2026, 5, 5, 10, 0),
                        null,
                        null,
                        null,
                        ParkingEventType.ENTRY
                ));

        mockMvc.perform(post("/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "license_plate": "ABC1234",
                                  "entry_time": "2026-05-05T10:00:00",
                                  "event_type": "ENTRY"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn500WhenUseCaseThrowsUnexpectedException() throws Exception {
        when(mapper.toCommand(any()))
                .thenReturn(new ParkingEventCommand(
                        "ABC1234",
                        LocalDateTime.of(2026, 5, 5, 10, 0),
                        null,
                        null,
                        null,
                        ParkingEventType.ENTRY
                ));

        doThrow(new RuntimeException("unexpected error"))
                .when(processParkingEvent)
                .execute(any());

        mockMvc.perform(post("/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "license_plate": "ABC1234",
                                  "entry_time": "2026-05-05T10:00:00",
                                  "event_type": "ENTRY"
                                }
                                """))
                .andExpect(status().isInternalServerError());
    }
}