package com.airlinespoo.locations.model;

import com.airlinespoo.core.model.Identifiable;
import com.airlinespoo.core.util.Validator;

import java.util.Objects;

public class City implements Identifiable<Integer> {

    private static final int MAX_NAME_LENGTH = 20;

    private final int id;
    private String name;

    public City(int id, String name) {
        this.id = (int) Validator.requirePositive(id, "id de la ciudad");
        this.name = Validator.requireText(name, "nombre de la ciudad", MAX_NAME_LENGTH);
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Validator.requireText(name, "nombre de la ciudad", MAX_NAME_LENGTH);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof City)) return false;
        City other = (City) o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "City{id=" + id + ", name='" + name + "'}";
    }
}

