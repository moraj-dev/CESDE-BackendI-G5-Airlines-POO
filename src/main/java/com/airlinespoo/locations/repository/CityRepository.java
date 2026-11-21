package com.airlinespoo.locations.repository;

import com.airlinespoo.core.repository.InMemoryRepository;
import com.airlinespoo.locations.model.City;

import java.util.List;
import java.util.Optional;

public class CityRepository extends InMemoryRepository<City, Integer> {

    @Override
    protected String entityName() {
        return "ciudad";
    }

    public Optional<City> findByName(String name) {
        return values().stream()
            .filter(city -> city.getName().equalsIgnoreCase(name))
            .findFirst();
    }

    public List<City> searchByName(String text) {
        return values().stream()
            .filter(city -> city.getName().toLowerCase().contains(text.toLowerCase()))
            .toList();
    }
}
