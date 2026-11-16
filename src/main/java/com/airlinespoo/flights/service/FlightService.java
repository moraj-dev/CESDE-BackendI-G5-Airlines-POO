package com.airlinespoo.flights.service;

import com.airlinespoo.core.exception.NotFoundException;
import com.airlinespoo.flights.model.Flight;
import com.airlinespoo.flights.repository.FlightRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class FlightService {

    private final FlightRepository flightRepository;

    //Constructor
    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    //Métodos
    // Busca un vuelo por Id y si no lo encuentra lanza un mensaje de error
    public Flight getFlight(Integer id){
        return flightRepository.findById(id).orElseThrow(() -> new NotFoundException("vuelo", id));
    }

    //Busca un vuelo por nombre y si no lo encuentra lanza un mensaje de error
    public Flight getFlightByName(String flightName){
        return flightRepository.findByFlightName(flightName).orElseThrow(()-> new NotFoundException("vuelo", flightName));
    }

    //Lista de vuelos ordenados por fecha y hora de salida (departure)
    public List<Flight> listFlights(){
        List<Flight> flights=flightRepository.findAll();
        Collections.sort(flights);
        return flights;
    }

    //Eliminación de un vuelo mediante id
    public void deleteFlight(Integer id){
        if(!flightRepository.deleteById(id)){
            throw new NotFoundException("vuelo", id);
        }
    }

    //Mostrar los vuelos por aerolínea
    public List<Flight> listFlightsByAirline(Airline airline){
        List<Flight> flights=flightRepository.findByAirline(airline);
        Collections.sort(flights);
        return flights;
    }

    //Mostrar los vuelos entre dos fechas
    public List<Flight> listFlightsBetween(LocalDate start, LocalDate end){
        if(start.isAfter(end)){
            throw new IllegalArgumentException("La fecha final no puede ser anterior a la inicial");
        }
        List<Flight> flights=flightRepository.findByDepartureDateBetween(start, end);
        Collections.sort(flights);
        return flights;
    }

    //Mostrar los vuelos por ruta
    public List<Flight> listFlightsByRoute(Airport departure, Airport arrival){
        List<Flight> flights=flightRepository.findByRoute(departure, arrival);
        /*
        ********************************************************
        PENDIENTE VALIDAR LOS AEROPUERTOS SEGÚN SUS SERVICIOS
        ********************************************************
        */
        Collections.sort(flights);
        return  flights;
    }

    //Contar los vuelos por aerolíneas
    public long countFlightsByAirline(Airline airline){
        return flightRepository.countByAirline(airline);
    }

    //Validar si una aerolínea tiene vuelos
    public boolean airlineHasFlights(Airline airline){
        return flightRepository.existsByAirline(airline);
    }

    //Validar si un aeropuerto tiene vuelos
    public boolean airportHasFlights(Airport airport){
        return flightRepository.existsByAirport(airport);
    }

    public Map<Airline, List<Flight>> listFlightsByAirlineinMonth(int year, int month){
        if(flight.getDepartureDate().getYear() == year && ){

        }
    }

}
