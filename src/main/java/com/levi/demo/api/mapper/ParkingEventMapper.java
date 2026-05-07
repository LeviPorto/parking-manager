package com.levi.demo.api.mapper;

import com.levi.demo.api.dto.ParkingEventRequest;
import com.levi.demo.application.usecase.command.ParkingEventCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")public interface ParkingEventMapper {

    ParkingEventCommand toCommand(ParkingEventRequest request);

}
