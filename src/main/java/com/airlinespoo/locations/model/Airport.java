package com.airlinespoo.locations.model;

import com.airlinespoo.core.model.Identifiable;
import com.airlinespoo.core.util.Validator;

import java.util.Objects;
import java.util.UUID;

public class Airport implements Identifiable<UUID> {

    public static final int MAX_NAME_LENGTH = 50;

    private final UUID id;
    private String name;
    private City city;

    public Airport(String name, City city) {
        this(UUID.randomUUID(), name, city);
    }


    public Airport(UUID id, String name, City city) {
        this.id = Validator.requireNotNull(id, "id del aeropuerto");
        this.name = Validator.requireText(name, "nombre del aeropuerto", MAX_NAME_LENGTH);
        this.city = Validator.requireNotNull(city, "ciudad del aeropuerto");
    }

    @Override
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public City getCity() {
        return city;
    }

    public void setName(String name) {
        this.name = Validator.requireText(name, "nombre del aeropuerto", MAX_NAME_LENGTH);
    }

    public void setCity(City city) {
        this.city = Validator.requireNotNull(city, "ciudad del aeropuerto");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Airport)) return false;
        Airport other = (Airport) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Airport{name='" + name + "', city=" + city + "}";
    }
}

