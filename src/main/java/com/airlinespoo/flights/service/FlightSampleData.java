package com.airlinespoo.flights.service;

import com.airlinespoo.core.exception.ValidationException;
// imports de Airport, Airline, LocationService, AirlineService (rutas de tus compañeros)

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public final class FlightSampleData {

    private FlightSampleData() {
        // Clase utilitaria: no se instancia.
    }

    public static void load(FlightService flightService,
                            LocationService locationService,
                            AirlineService airlineService) {
        List<Airport> airports = locationService.listAirports();   // SUPUESTO: nombre del método
        List<Airline> airlines = airlineService.listAirlines();    // SUPUESTO: nombre del método

        if (airports.size() < 4 || airlines.size() < 2) {
            throw new ValidationException(
                "Los datos de ejemplo de vuelos necesitan al menos 4 aeropuertos y 2 aerolíneas.");
        }

        Airport a0 = airports.get(0);
        Airport a1 = airports.get(1);
        Airport a2 = airports.get(2);
        Airport a3 = airports.get(3);
        Airline first = airlines.get(0);
        Airline second = airlines.get(1);

        // Diciembre 2026: 5 vuelos, de las dos aerolíneas
        schedule(flightService, "AV101", a0, a1, LocalDateTime.of(2026, 12, 5, 8, 0),   Duration.ofMinutes(70),  first);
        schedule(flightService, "AV102", a1, a0, LocalDateTime.of(2026, 12, 5, 18, 30), Duration.ofMinutes(70),  first);
        schedule(flightService, "LT201", a0, a2, LocalDateTime.of(2026, 12, 12, 23, 30), Duration.ofMinutes(100), second); // nocturno: llega 01:10 del día siguiente
        schedule(flightService, "LT202", a2, a3, LocalDateTime.of(2026, 12, 20, 6, 15),  Duration.ofMinutes(125), second);
        schedule(flightService, "AV103", a2, a1, LocalDateTime.of(2026, 12, 28, 14, 0),  Duration.ofMinutes(90),  first);

        // Enero 2027: 3 vuelos
        schedule(flightService, "LT203", a3, a0, LocalDateTime.of(2027, 1, 4, 10, 45),  Duration.ofMinutes(115), second);
        schedule(flightService, "AV104", a1, a3, LocalDateTime.of(2027, 1, 11, 21, 20), Duration.ofMinutes(190), first);  // nocturno: llega 00:30
        schedule(flightService, "LT204", a0, a3, LocalDateTime.of(2027, 1, 18, 7, 0),   Duration.ofMinutes(140), second);
    }

    // Calcula la llegada a partir de la salida y la duración, y delega todo en el servicio
    private static void schedule(FlightService service, String code,
                                 Airport departure, Airport arrival,
                                 LocalDateTime departureAt, Duration duration, Airline airline) {
        LocalDateTime arrivalAt = departureAt.plus(duration);
        service.createFlight(code, departure, arrival,
            departureAt.toLocalDate(), departureAt.toLocalTime(),
            arrivalAt.toLocalDate(), arrivalAt.toLocalTime(), airline);
    }
}
