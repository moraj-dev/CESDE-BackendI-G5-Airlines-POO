package com.airlinespoo.flights.service;

import com.airlinespoo.core.exception.DuplicateException;
import com.airlinespoo.core.exception.NotFoundException;
import com.airlinespoo.core.exception.ValidationException;
import com.airlinespoo.core.util.IdGenerator;
import com.airlinespoo.core.util.Validator;
import com.airlinespoo.flights.model.Flight;
import com.airlinespoo.flights.repository.FlightRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;

public class FlightService {

    private final FlightRepository flightRepository;
    private final IdGenerator idGenerator;
    private final LocationService locationService;
    private final AirlineService airlineService;

    //Constructor
    public FlightService(FlightRepository flightRepository,
                         IdGenerator idGenerator,
                         AirportService airportService,
                         AirlineService airlineService) {
        this.flightRepository = flightRepository;
        this.idGenerator = idGenerator;
        this.airportService=airportService;
        this.airlineService=airlineService;
    }

    //Métodos
    //CREATE
    //Crear un vuelo
    public Flight createFlight(String flightName, Airport departure, Airport arrival,
                               LocalDate departureDate, LocalTime departureTime,
                               LocalDate arrivalDate, LocalTime arrivalTime, Airline airline) {
        requireExistingReferences(departure, arrival, airline);

        int id = idGenerator.peekNext();
        Flight flight = new Flight(id, flightName, departure, arrival,
            departureDate, departureTime, arrivalDate, arrivalTime, airline);
        ensureUniqueName(flight.getFlightName(), id);

        flightRepository.create(flight);
        idGenerator.next();
        return flight;
    }
    //READ
    // Busca un vuelo por Id y si no lo encuentra lanza un mensaje de error
    public Flight getFlight(Integer id){
        return flightRepository.findById(id).orElseThrow(() -> new NotFoundException("vuelo", id));
    }

    //Busca un vuelo por nombre y si no lo encuentra lanza un mensaje de error
    public Flight getFlightByName(String flightName) {
        String name = Flight.normalizeFlightName(flightName);
        return flightRepository.findByFlightName(name)
            .orElseThrow(() -> new NotFoundException(
                "No existe un vuelo con el código '%s'.".formatted(name)));
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
        requireExistingAirline(airline);
        List<Flight> flights=flightRepository.findByAirline(airline);
        Collections.sort(flights);
        return flights;
    }

    //Mostrar los vuelos entre dos fechas
    public List<Flight> listFlightsBetween(LocalDate start, LocalDate end){
        Validator.requireNotNull(start,"fecha inicial");
        Validator.requireNotNull(end, "fecha final");
        if(start.isAfter(end)){
            throw new ValidationException("La fecha final no puede ser anterior a la inicial");
        }
        List<Flight> flights=flightRepository.findByDepartureDateBetween(start, end);
        Collections.sort(flights);
        return flights;
    }

    //Mostrar los vuelos por ruta
    public List<Flight> findByRoute(Airport departure, Airport arrival) {
        List<Flight> result = new ArrayList<>();
        for (Flight flight : values()) {
            if (flight.getDeparture().equals(departure) && flight.getArrival().equals(arrival)) {
                result.add(flight);
            }
        }
        result.sort(null);
        return result;
    }

    //Contar los vuelos por aerolíneas
    public long countFlightsByAirline(Airline airline){
        requireExistingAirline(airline);
        return flightRepository.countByAirline(airline);
    }

    //Validar si una aerolínea tiene vuelos
    public boolean airlineHasFlights(Airline airline){
        requireExistingAirline(airline);
        return flightRepository.existsByAirline(airline);
    }

    //Validar si un aeropuerto tiene vuelos
    public boolean airportHasFlights(Airport airport){
        return flightRepository.existsByAirport(airport);
    }

    public Map<Airline, List<Flight>> listFlightsByAirlineInMonth(int year, int month) {
        Validator.requireInRange(month, 1, 12, "mes");
        Validator.requireInRange(year, 1900, 2100, "año");

        YearMonth period = YearMonth.of(year, month);
        List<Flight> flights = flightRepository
            .findByDepartureDateBetween(period.atDay(1), period.atEndOfMonth());

        Map<Airline, List<Flight>> report = new TreeMap<>(
            Comparator.comparing(Airline::getName, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Airline::getId));
        for (Flight flight : flights) {
            report.computeIfAbsent(flight.getAirline(), k -> new ArrayList<>()).add(flight);
        }
        return report;
    }
    //UPDATE
    public Flight updateFlight(int id, String flightName, Airport departure, Airport arrival,
                               LocalDate departureDate, LocalTime departureTime,
                               LocalDate arrivalDate, LocalTime arrivalTime, Airline airline) {
        getFlight(id);
        requireExistingReferences(departure, arrival, airline);

        Flight updated = new Flight(id, flightName, departure, arrival,
            departureDate, departureTime, arrivalDate, arrivalTime, airline);
        ensureUniqueName(updated.getFlightName(), id);
        return flightRepository.update(updated);
    }

    private void ensureUniqueName(String name, int idToIgnore) {
        flightRepository.findByFlightName(name)
            .filter(other -> other.getId() != idToIgnore)
            .ifPresent(other -> {
                throw new DuplicateException(
                    "Ya existe un vuelo con el código '%s'.".formatted(name));
            });
    }

    private void requireExistingReferences(Airport departure, Airport arrival, Airline airline) {
        Validator.requireNotNull(departure, "aeropuerto de salida");
        Validator.requireNotNull(arrival, "aeropuerto de llegada");
        Validator.requireNotNull(airline, "aerolínea");
        locationService.getAirport(departure.getId());
        locationService.getAirport(arrival.getId());
        airlineService.getAirline(airline.getId());
    }

    private void requireExistingAirline(Airline airline) {
        Validator.requireNotNull(airline, "aerolínea");
        locationService.getAirline(airline.getId());   // lanza NotFoundException si no existe
    }

}
