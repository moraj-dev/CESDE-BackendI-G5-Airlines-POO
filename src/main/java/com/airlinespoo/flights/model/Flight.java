package com.airlinespoo.flights.model;

import com.airlinespoo.core.exception.ValidationException;
import com.airlinespoo.core.model.Identifiable;
import com.airlinespoo.core.util.Validator;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

public class Flight implements Comparable<Flight>, Identifiable<Integer> {
    //Atributos
    private final int id;
    private String flightName;
    private Airport departure;
    private Airport arrival;
    private LocalDate departureDate;
    private LocalTime departureTime;
    private LocalDate arrivalDate;
    private LocalTime arrivalTime;
    private Airline airline;

    //Constructor

    public Flight(final int id, String flightName, Airport departure, Airport arrival, LocalDate departureDate, LocalTime departureTime, LocalDate arrivalDate, LocalTime arrivalTime, Airline airline) {
        this.id = id;

        //Validación del nombre del vuelo
        this.flightName = normalizeFlightName(flightName);

        //Validación de que los aeropuertos no sean nulos y además sean diferentes
        this.departure = Validator.requireNotNull(departure, "aeropuerto de salida");
        this.arrival = Validator.requireNotNull(arrival, "aeropuerto de llegada");
        if (departure.equals(arrival)) {
            throw new ValidationException("Los aeropuertos deben ser diferentes");
        }

        this.departureDate = Validator.requireNotNull(departureDate, "fecha de salida");
        this.departureTime = Validator.requireNotNull(departureTime, "hora de salida");
        this.arrivalDate = Validator.requireNotNull(arrivalDate, "fecha de llegada");
        this.arrivalTime = Validator.requireNotNull(arrivalTime, "hora de llegada");

        //Unión de fecha y hora
        LocalDateTime departureDateTime = departureDate.atTime(departureTime);
        LocalDateTime arrivalDateTime = arrivalDate.atTime(arrivalTime);

        //Validación que la fecha y hora de llegada sea posterior a la de salida
        if (!arrivalDateTime.isAfter(departureDateTime)) {
            throw new ValidationException("La fecha y hora de llegada debe ser posterior a la fecha y hora de salida");
        }

        this.airline = Validator.requireNotNull(airline, "aerolínea");
    }

    //Métodos

    // Unir hora y fecha del vuelo de salida
    public LocalDateTime getDepartureDateTime() {
        return departureDate.atTime(departureTime);
    }

    //Unir hora y fecha del vuelo de llegada
    public LocalDateTime getArrivalDateTime() {
        return arrivalDate.atTime(arrivalTime);
    }

    //Calcular la duración del vuelo
    public Duration getFlightDuration() {
        return Duration.between(getDepartureDateTime(), getArrivalDateTime());
    }

    //Validar si un vuelo ya salió o no
    public boolean hasDeparted(LocalDateTime dateTimeToCompare) {
        Validator.requireNotNull(dateTimeToCompare,"fecha y hora de referencia");
        return !dateTimeToCompare.isBefore(getDepartureDateTime());
    }

    //Interfaz Comparable
    @Override
    public int compareTo(Flight other) {
        int result = getDepartureDateTime().compareTo(other.getDepartureDateTime());
        return result !=0? result:Integer.compare(id, other.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else {
            if (obj instanceof Flight) {
                return id == ((Flight) obj).id;
            } else return false;
        }
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    //Convertir a cadena de texto el objeto sobreescribiéndolo
    @Override
    public String toString() {
        return "- Vuelo:                  " + getFlightName() +
            "\n- ID:                      " + getId() +
            "\n- Aeropuerto de salida:    " + departure.getName() +
            "\n- Aeropuerto de llegada:   " + arrival.getName() +
            "\n- Nombre de la aerolínea:  " + airline.getName() +
            "\n- Fecha y hora de salida:  " + getDepartureDateTime() +
            "\n- Fecha y hora de llegada: " + getArrivalDateTime();
    }

    public static String normalizeFlightName(String value) {
        String name = Validator.requireText(value, "código del vuelo", 8)
            .toUpperCase(Locale.ROOT);
        if (!name.matches("[A-Z0-9]+")) {
            throw new ValidationException(
                "El campo 'código del vuelo' solo admite letras y dígitos, sin espacios ni símbolos.");
        }
        return name;
    }

    //Getters
    @Override
    public Integer getId() {
        return id;
    }

    public String getFlightName() {
        return flightName;
    }

    public Airport getDeparture() {
        return departure;
    }

    public Airport getArrival() {
        return arrival;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public Airline getAirline() {
        return airline;
    }
}
