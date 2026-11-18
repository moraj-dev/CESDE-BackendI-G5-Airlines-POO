package com.airlinespoo.flights.service;

import com.airlinespoo.core.exception.DuplicateException;
import com.airlinespoo.core.exception.NotFoundException;
import com.airlinespoo.flights.model.Flight;
import com.airlinespoo.flights.repository.FlightRepository;

import java.time.LocalDate;
import java.util.*;

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

    public Map<Airline, List<Flight>> listFlightsByAirlineInMonth(int year, int month){
        //*******************************************************************
        //Pendiente la validación de Airline para cambiar Hashmap por TreeMap
        //*******************************************************************
        //Validación de que el mes sea un número válido
        if(month<1 || month >12){
            throw new IllegalArgumentException("El mes debe estar entre 1 y 12");
        }
        //Validación que el año sea un número válido
        if(year<1900 || year >2100){
            throw new IllegalArgumentException("Digita un año entre 1900 y 2100");
        }

        Map<Airline,List<Flight>> flights=new HashMap<>();
        List<Flight> flightsAll = flightRepository.findAll();
        for(Flight flight: flightsAll){
            if(flight.getDepartureDate().getYear()==year && flight.getDepartureDate().getMonthValue()==month){
                Airline airline=flight.getAirline();
                List <Flight> flightsByAirline=flights.computeIfAbsent(airline, k-> new ArrayList<>());
                flightsByAirline.add(flight);

            }
        }
        for(List<Flight> listFlight: flights.values()){
            Collections.sort(listFlight);
        }
        return flights;
    }

    //Crear un vuelo
    public Flight createFlight(Flight flight){
        if(flightRepository.findByFlightName(flight.getFlightName()).isPresent()){
            throw new DuplicateException("vuelo",flight.getFlightName());
        }
        //**********************************************************************************************
        //Pendiente la validación de existencia de aeropuertos de salida y llegada por el airportService
        //**********************************************************************************************
    }

}
