package com.flowable.training.dp;

import java.util.stream.Stream;

import org.flowable.engine.delegate.BpmnError;
import org.springframework.stereotype.Component;

@Component
public class FlightService {

    public void bookFlight(boolean serviceAvailable, String from, String to) {
        if (!serviceAvailable) {
            throw new RuntimeException("Service is not available");
        }

        if (from.equalsIgnoreCase(to)) {
            throw new BpmnError("sameAirport");
        }

        if (Stream.of(from, to).anyMatch(this::isLongHaul)) {
            throw new BpmnError("longHaul");
        }
    }

    private boolean isLongHaul(String airport) {
        if (airport.equalsIgnoreCase("DEN") || airport.equalsIgnoreCase("SIN")) {
            return true;
        }

        return false;
    }

}
