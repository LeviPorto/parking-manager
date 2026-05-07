package com.levi.demo.api.mapper;

import com.levi.demo.api.dto.RevenueResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface RevenueMapper {

    @Mapping(target = "currency", constant = "BRL")
    @Mapping(target = "timestamp",
            expression = "java(java.time.LocalDateTime.now())")
    RevenueResponse toResponse(BigDecimal amount);

}
