package com.levi.demo.infrastructure.bootstrap;

import com.levi.demo.application.port.in.InitializeGarage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GarageInitializerTest {

    @Mock
    private InitializeGarage initializeGarage;

    @Mock
    private ApplicationArguments arguments;

    @InjectMocks
    private GarageInitializer initializer;

    @Test
    void shouldInitializeGarageSuccessfully() {
        assertDoesNotThrow(() -> initializer.run(arguments));

        verify(initializeGarage).execute();
    }

    @Test
    void shouldNotThrowWhenInitializationFails() {
        doThrow(new RuntimeException("Simulator unavailable"))
                .when(initializeGarage)
                .execute();

        assertDoesNotThrow(() -> initializer.run(arguments));

        verify(initializeGarage).execute();
    }
}