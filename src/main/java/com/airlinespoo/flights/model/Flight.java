package com.airlinespoo.flights.model;

import com.airlinespoo.core.model.Identifiable;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Objects;

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
        this.flightName = validateFlightName(flightName);

        //Validación de que los aeropuertos no sean nulos y además sean diferentes
        this.departure = Objects.requireNonNull(departure, "El aeropuerto de salida no puede estar vacío");
        this.arrival = Objects.requireNonNull(arrival, "El aeropuerto de llegada no puede estar vacío");
        if (departure.equals(arrival)) {
            throw new IllegalArgumentException("Los aeropuertos deben ser diferentes");
        }

        this.departureDate = Objects.requireNonNull(departureDate, "La fecha de salida no puede estar vacía");
        this.departureTime = Objects.requireNonNull(departureTime, "La hora de salida no puede estar vacía");
        this.arrivalDate = Objects.requireNonNull(arrivalDate, "La fecha de llegada no puede estar vacía");
        this.arrivalTime = Objects.requireNonNull(arrivalTime, "La hora de llegada no puede estar vacía");

        //Unión de fecha y hora
        LocalDateTime departureDateTime = departureDate.atTime(departureTime);
        LocalDateTime arrivalDateTime = arrivalDate.atTime(arrivalTime);

        //Validación que la fecha y hora de llegada sea posterior a la de salida
        if (departureDateTime.compareTo(arrivalDateTime) >= 0) {
            throw new IllegalArgumentException("La fecha y hora de llegada debe ser posterior a la fecha y hora de salida");
        }

        this.airline = Objects.requireNonNull(airline, "La aerolínea no puede estar vacía");
    }

    //Métodos
    public LocalDateTime getDepartureDateTime() {
        return departureDate.atTime(departureTime);
    }

    public LocalDateTime getArrivalDateTime() {
        return arrivalDate.atTime(arrivalTime);
    }

    public Duration getFlightDuration() {
        return Duration.between(getDepartureDateTime(), getArrivalDateTime());
    }

    public boolean hasDeparted(LocalDateTime dateTimeToCompare) {
        return dateTimeToCompare.compareTo(getDepartureDateTime()) >= 0;
    }

    //Interfaz Comparable
    @Override
    public int compareTo(Flight other) {
        return getDepartureDateTime().compareTo(other.getDepartureDateTime());
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
        return Integer.hashCode(getId());
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

    //Reprogramar vuelo
    public void rescheduleFlight(LocalDateTime newDepartureDateTime) {
        Objects.requireNonNull(newDepartureDateTime, "El campo fecha y hora no puede estar vacío.");
        Duration originalDuration = getFlightDuration();
        LocalDateTime newArrivalDateTime = newDepartureDateTime.plus(originalDuration);
        this.departureDate = (newDepartureDateTime.toLocalDate());
        this.departureTime = (newDepartureDateTime.toLocalTime());
        this.arrivalDate = (newArrivalDateTime.toLocalDate());
        this.arrivalTime = (newArrivalDateTime.toLocalTime());
    }

    private String validateFlightName(String flightName) {
        if (flightName == null || flightName.isBlank()) {
            throw new IllegalArgumentException("El elemento está vacío");
        } else {
            flightName = flightName.toUpperCase(Locale.ROOT);
            if (flightName.matches("^[A-Z0-9]{1,8}$")) {
                return flightName;
            } else
                throw new IllegalArgumentException("el nombre debe tener máximo 8 caracteres, letras mayúsculas de la A a la Z y números sin símbolos");
        }

    }

    //Getters

    public int getId() {
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

    //Setters

    public void setFlightName(String flightName) {
        this.flightName = validateFlightName(flightName);
    }
}
