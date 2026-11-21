package com.airlinespoo.locations.service;

import com.airlinespoo.core.exception.BusinessRuleException;
import com.airlinespoo.core.exception.DuplicateException;
import com.airlinespoo.core.exception.NotFoundException;
import com.airlinespoo.core.util.IdGenerator;
import com.airlinespoo.core.util.Validator;
import com.airlinespoo.locations.model.Airport;
import com.airlinespoo.locations.model.City;
import com.airlinespoo.locations.repository.AirportRepository;
import com.airlinespoo.locations.repository.CityRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LocationService {

    private final CityRepository cityRepository;
    private final IdGenerator idGenerator;
    private final AirportRepository airportRepository;

    public LocationService(CityRepository cityRepository, AirportRepository airportRepository, IdGenerator idGenerator) {
        this.cityRepository = cityRepository;
        this.idGenerator = idGenerator;
        this.airportRepository = airportRepository;
    }

    public City createCity(String name) {
        String cleanName = Validator.requireText(name, "nombre de la ciudad", City.MAX_NAME_LENGTH);
        if (cityRepository.findByName(cleanName).isPresent()) {
            throw new DuplicateException("Ya existe una ciudad con el nombre '" + cleanName + "'.");
        }
        City city = new City(idGenerator.next(), cleanName);
        return cityRepository.create(city);
    }

    public City getCity(int id) {
        Optional<City> result = cityRepository.findById(id);
        if (result.isEmpty()) {
            throw new NotFoundException("ciudad", id);
        }
        return result.get();
    }

    public List<City> listCities() {
        List<City> cities = cityRepository.findAll();
        cities.sort(Comparator.comparing(City::getName));
        return cities;
    }

    public City updateCity(int id, String newName) {
        City city = getCity(id);
        String cleanName = Validator.requireText(newName, "nombre de la ciudad", City.MAX_NAME_LENGTH);
        Optional<City> sameName = cityRepository.findByName(cleanName);
        if (sameName.isPresent() && !sameName.get().equals(city)) {
            throw new DuplicateException("Ya existe una ciudad con el nombre '" + cleanName + "'.");
        }
        city.setName(cleanName);
        return cityRepository.update(city);
    }

    public Airport createAirport(String name, int cityId) {
        String cleanName = Validator.requireText(name, "nombre del aeropuerto", Airport.MAX_NAME_LENGTH);
        City city = getCity(cityId);
        if (airportRepository.findByCityAndName(city, cleanName).isPresent()) {
            throw new DuplicateException("Ya existe un aeropuerto con el nombre '" + cleanName + "' en esa ciudad.");
        }
        return airportRepository.create(new Airport(cleanName, city));
    }

    public List<City> searchCitiesByName(String text) {
        return cityRepository.searchByName(text);
    }


    public void deleteCity(int id) {
        City city = getCity(id);
        if (airportRepository.countByCity(city) > 0) {
            throw new BusinessRuleException("No se puede eliminar la ciudad porque tiene aeropuertos.");
        }
        cityRepository.deleteById(id);
    }

    public Airport getAirport(UUID id) {
        Optional<Airport> result = airportRepository.findById(id);
        if (result.isEmpty()) {
            throw new NotFoundException("aeropuerto", id);
        }
        return result.get();
    }

    public List<Airport> listAirports() {
        List<Airport> airports = airportRepository.findAll();
        airports.sort(Comparator.comparing(Airport::getName));
        return airports;
    }

    public List<Airport> listAirportsByCity(int cityId) {
        City city = getCity(cityId);
        List<Airport> airports = airportRepository.findByCity(city);
        airports.sort(Comparator.comparing(Airport::getName));
        return airports;
    }

    public Airport updateAirport(UUID id, String newName, int newCityId) {
        Airport airport = getAirport(id);
        String cleanName = Validator.requireText(newName, "nombre del aeropuerto", Airport.MAX_NAME_LENGTH);
        City city = getCity(newCityId);
        Optional<Airport> same = airportRepository.findByCityAndName(city, cleanName);
        if (same.isPresent() && !same.get().equals(airport)) {
            throw new DuplicateException("Ya existe un aeropuerto con el nombre'" + cleanName + "'en esa ciudad.");
        }
        airport.setName(cleanName);
        airport.setCity(city);
        return airportRepository.update(airport);
    }

    public void deleteAirport(UUID id) {
        getAirport(id);
        airportRepository.deleteById(id);
    }

    public List<Airport> searchAirportsByName(String text) {
        return airportRepository.searchByName(text);
    }

    public long countAirportsByCity(int cityId) {
        return airportRepository.countByCity(getCity(cityId));
    }
}
