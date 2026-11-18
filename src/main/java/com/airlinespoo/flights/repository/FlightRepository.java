package com.airlinespoo.flights.repository;

import com.airlinespoo.core.repository.InMemoryRepository;
import com.airlinespoo.flights.model.Flight;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FlightRepository extends InMemoryRepository<Flight, Integer> {
    @Override
    protected String entityName() {
        return "vuelo";
    }

    //Encontrar el vuelo por nombre
    public Optional<Flight> findByFlightName(String flightName){
        List<Flight> listFlights = values();
        for(Flight flight: listFlights){
            if(flight.getFlightName().equals(flightName)){
                return Optional.of(flight);
            }
        }
        return Optional.empty();
    }

    //Encontrar los vuelos por aerolínea
    public List<Flight> findByAirline(Airline airline){
        List<Flight> flightsByAirline = new ArrayList<>();
        for(Flight flight: values()){
            if(flight.getAirline().equals(airline)){
                flightsByAirline.add(flight);
            }
        }
        return flightsByAirline;
    }

    //Encontrar vuelos entre dos fechas
    public List<Flight> findByDepartureDateBetween(LocalDate start, LocalDate end){
        List<Flight> flightsByDateBetween = new ArrayList<>();
        if(start.isAfter(end)){
            throw new IllegalArgumentException("La fecha final no puede ser anterior a la fecha inicial");
        }
        for(Flight flight:values()){
            if((start.isBefore(flight.getDepartureDate()) || start.isEqual(flight.getDepartureDate())) && (end.isAfter(flight.getDepartureDate()) || end.isEqual(flight.getDepartureDate()))){
                flightsByDateBetween.add(flight);
            }
        }
        return flightsByDateBetween;
    }
    //Listar los vuelos por aeropuerto de llegada y aeropuerto de salida
    public List<Flight> findByRoute(Airport departure, Airport arrival){
        List<Flight> flightsByRoute = new ArrayList<>();
        for(Flight flight:values()){
            if(flight.getDeparture().equals(departure) && flight.getArrival().equals(arrival)){
                flightsByRoute.add(flight);
            }
        }
        return flightsByRoute;
    }

    //Contar los vuelos por aerolíneas
    public long countByAirline(Airline airline){
        long count=0;
        for(Flight flight:values()){
            if(airline.equals(flight.getAirline())){
                count++;
            }
        }
        return count;
    }

    //Validar si las aerolíneas tienen vuelos
    public boolean existsByAirline (Airline airline){
        for(Flight flight:values()){
            if(flight.getAirline().equals(airline)){
                return true;
            }
        }
        return false;
    }

    //Validar si los aeropuertos tienen vuelos
    public boolean existsByAirport(Airport airport){
        for(Flight flight:values()){
            if(flight.getDeparture().equals(airport)||flight.getArrival().equals(airport)){
                return true;
            }
        }
        return false;
    }
}

