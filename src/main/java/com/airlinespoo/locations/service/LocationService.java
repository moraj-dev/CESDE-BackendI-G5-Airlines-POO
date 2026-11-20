package com.airlinespoo.locations.service;

import com.airlinespoo.core.exception.DuplicateException;
import com.airlinespoo.core.exception.NotFoundException;
import com.airlinespoo.core.util.IdGenerator;
import com.airlinespoo.core.util.Validator;
import com.airlinespoo.locations.model.City;
import com.airlinespoo.locations.repository.CityRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class LocationService {

    private final CityRepository cityRepository;
    private final IdGenerator idGenerator;

    public LocationService(CityRepository cityRepository, IdGenerator idGenerator) {
        this.cityRepository = cityRepository;
        this.idGenerator = idGenerator;
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

    public List<City> searchCitiesByName(String text) {
        return cityRepository.searchByName(text);
    }
}

