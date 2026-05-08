package com.levi.demo.api.controller;

import com.levi.demo.api.dto.RevenueResponse;
import com.levi.demo.api.exception.GlobalExceptionHandler;
import com.levi.demo.api.mapper.RevenueMapper;
import com.levi.demo.application.port.in.GetRevenue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RevenueController.class)
@Import(GlobalExceptionHandler.class)
class RevenueControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetRevenue getRevenue;

    @MockBean
    private RevenueMapper revenueMapper;

    @Test
    void shouldReturn200WhenRevenueRequestIsValid() throws Exception {
        when(getRevenue.retrieve(eq(LocalDate.of(2026, 5, 5)), eq("A")))
                .thenReturn(new BigDecimal("120.50"));

        when(revenueMapper.toResponse(new BigDecimal("120.50")))
                .thenReturn(new RevenueResponse(
                        new BigDecimal("120.50"),
                        "BRL",
                        LocalDateTime.of(2026, 5, 5, 10, 0)
                ));

        mockMvc.perform(get("/revenue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\": \"2026-05-05\", \"sector\": \"A\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn500WhenUseCaseThrowsUnexpectedException() throws Exception {
        doThrow(new RuntimeException("unexpected error"))
                .when(getRevenue)
                .retrieve(eq(LocalDate.of(2026, 5, 5)), eq("A"));

        mockMvc.perform(get("/revenue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\": \"2026-05-05\", \"sector\": \"A\"}"))
                .andExpect(status().isInternalServerError());
    }
}