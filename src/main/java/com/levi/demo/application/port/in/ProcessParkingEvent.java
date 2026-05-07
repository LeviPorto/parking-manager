package com.levi.demo.application.port.in;

import com.levi.demo.application.usecase.command.ParkingEventCommand;

public interface ProcessParkingEvent {
    void execute(ParkingEventCommand command);
}
