package com.airlinespoo.locations.ui;

import com.airlinespoo.core.exception.AirlinesException;
import com.airlinespoo.core.ui.ConsoleInput;
import com.airlinespoo.core.ui.Menu;
import com.airlinespoo.locations.model.City;
import com.airlinespoo.locations.service.LocationService;

import java.util.List;

public class LocationMenu implements Menu {

    private final LocationService locationService;
    private final ConsoleInput input;

    public LocationMenu(LocationService locationService, ConsoleInput input) {
        this.locationService = locationService;
        this.input = input;
    }

    @Override
    public String title() {
        return "Ubicaciones";
    }

    @Override
    public void run() {
        boolean back = false;
        while (!back) {
            showOptions();
            int option = input.readInt("Elige una opción", 1, 5);
            try {
                switch (option) {
                    case 1 -> registerCity();
                    case 2 -> listCities();
                    case 3 -> searchCities();
                    case 4 -> renameCity();
                    case 5 -> back = true;
                }
            } catch (AirlinesException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void showOptions() {
        System.out.println("\n--- Ciudades ---");
        System.out.println("1. Registrar ciudad");
        System.out.println("2. Listar ciudades");
        System.out.println("3. Buscar ciudad por nombre");
        System.out.println("4. Renombrar ciudad");
        System.out.println("5. Volver");
    }

    private void registerCity() {
        String name = input.readRequiredText("Nombre de la ciudad");
        City city = locationService.createCity(name);
        System.out.println("Ciudad registrada: " + city);
    }

    private void listCities() {
        List<City> cities = locationService.listCities();
        if (cities.isEmpty()) {
            System.out.println("No hay ciudades registradas.");
            return;
        }
        for (City city : cities) {
            System.out.println(city.getId() + " - " + city.getName());
        }
    }

    private void searchCities() {
        String text = input.readRequiredText("Texto a buscar");
        List<City> found = locationService.searchCitiesByName(text);
        if (found.isEmpty()) {
            System.out.println("No se encontraron ciudades.");
            return;
        }
        for (City city : found) {
            System.out.println(city.getId() + " - " + city.getName());
        }
    }

    private void renameCity() {
        List<City> cities = locationService.listCities();
        if (cities.isEmpty()) {
            System.out.println("No hay ciudades registradas.");
            return;
        }
        for (int i = 0; i < cities.size(); i++) {
            System.out.println((i + 1) + ". " + cities.get(i).getName());
        }
        int choice = input.readInt("Elige la ciudad", 1, cities.size());
        City selected = cities.get(choice - 1);
        String newName = input.readRequiredText("Nuevo nombre");
        locationService.updateCity(selected.getId(), newName);
        System.out.println("Nombre actualizado.");
    }
}



