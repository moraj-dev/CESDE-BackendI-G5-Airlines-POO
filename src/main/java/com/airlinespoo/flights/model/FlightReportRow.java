package com.airlinespoo.flights.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Una fila del reporte "vuelos por aerolínea": equivale a una fila del resultado
 * del procedimiento uspVuelosPorAerolinea. Solo agrupa datos de lectura.
 */
public record FlightReportRow(
    String airlineName,
    String flightName,
    String departureCity,
    String departureAirport,
    LocalDate departureDate,
    LocalTime departureTime,
    String arrivalCity,
    String arrivalAirport,
    LocalDate arrivalDate,
    LocalTime arrivalTime) {

    /** Arma la fila a partir de un vuelo. */
    public static FlightReportRow from(Flight flight) {
        return new FlightReportRow(
            flight.getAirline().getName(),
            flight.getFlightName(),
            flight.getDeparture().getCity().getName(),
            flight.getDeparture().getName(),
            flight.getDepartureDate(),
            flight.getDepartureTime(),
            flight.getArrival().getCity().getName(),
            flight.getArrival().getName(),
            flight.getArrivalDate(),
            flight.getArrivalTime());
    }
}
