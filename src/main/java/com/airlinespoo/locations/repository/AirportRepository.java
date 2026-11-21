package com.airlinespoo.locations.repository;


import com.airlinespoo.core.repository.InMemoryRepository;
import com.airlinespoo.locations.model.Airport;
import com.airlinespoo.locations.model.City;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AirportRepository extends InMemoryRepository<Airport, UUID> {

    @Override
    protected String entityName() {
        return "aeropuerto";
    }

    public List<Airport> findByCity(City city) {
        List<Airport> result = new ArrayList<>();
        for (Airport airport : values()) {
            if (airport.getCity().equals(city)) {
                result.add(airport);
            }
        }
        return result;
    }

    public Optional<Airport> findByCityAndName(City city, String name) {
        for (Airport airport : values()) {
            if (airport.getCity().equals(city) && airport.getName().equalsIgnoreCase(name)) {
                return Optional.of(airport);
            }
        }
        return Optional.empty();
    }

    public List<Airport> searchByName(String text) {
        List<Airport> result = new ArrayList<>();
        for (Airport airport : values()) {
            if (airport.getName().toLowerCase().contains(text.toLowerCase())) {
                result.add(airport);
            }
        }
        return result;
    }

    public long countByCity(City city) {
        return findByCity(city).size();
    }
}

