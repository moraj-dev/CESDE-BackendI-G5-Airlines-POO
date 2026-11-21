package com.airlinespoo.locations.ui;

import com.airlinespoo.core.exception.AirlinesException;
import com.airlinespoo.core.exception.BusinessRuleException;
import com.airlinespoo.core.ui.ConsoleInput;
import com.airlinespoo.core.ui.Menu;
import com.airlinespoo.locations.model.Airport;
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
            int option = input.readInt("Elige una opción", 1, 10);
            try {
                switch (option) {
                    case 1 -> registerCity();
                    case 2 -> registerAirport();
                    case 3 -> listCities();
                    case 4 -> listAirportsByCity();
                    case 5 -> searchAirports();
                    case 6 -> searchCities();
                    case 7 -> renameCity();
                    case 8 -> deleteCity();
                    case 9 -> deleteAirport();
                    case 10 -> back = true;
                }
            } catch (AirlinesException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void showOptions() {
        System.out.println("\n--- Ubicaciones ---");
        System.out.println("1. Registrar ciudad");
        System.out.println("2. Registrar aeropuerto");
        System.out.println("3. Listar ciudades");
        System.out.println("4. Listar aeropuertos por ciudad");
        System.out.println("5. Buscar aeropuertos por nombre");
        System.out.println("6. Buscar ciudad por nombre");
        System.out.println("7. Renombrar ciudad");
        System.out.println("8. Eliminar ciudad");
        System.out.println("9. Eliminar aeropuerto");
        System.out.println("10. Volver");
    }

    private void registerCity() {
        String name = input.readRequiredText("Nombre de el aeropuerto");
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
        String text = input.readRequiredText("ciudad a buscar");
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

    private City chooseCity() {
        List<City> cities = locationService.listCities();
        if (cities.isEmpty()) {
            throw new BusinessRuleException("Primero registra una ciudad.");
        }
        for (int i = 0; i < cities.size(); i++) {
            System.out.println((i + 1) + ". " + cities.get(i).getName());
        }
        int choice = input.readInt("Elige la ciudad", 1, cities.size());
        return cities.get(choice - 1);
    }

    private void registerAirport() {
        City city = chooseCity();
        String name = input.readRequiredText("nombre de la ciudad");
        Airport airport = locationService.createAirport(name, city.getId());
        System.out.println("Aeropuerto registrado: " + airport);
    }

    private void listAirportsByCity() {
        City city = chooseCity();
        List<Airport> airports = locationService.listAirportsByCity(city.getId());
        if (airports.isEmpty()) {
            System.out.println("Esa ciudad no tiene aeropuertos.");
            return;
        }
        for (Airport airport : airports) {
            System.out.println("- " + airport.getName());
        }
    }

    private void searchAirports() {
        String text = input.readRequiredText("aeropuerto a buscar");
        List<Airport> found = locationService.searchAirportsByName(text);
        if (found.isEmpty()) {
            System.out.println("No se encontraron aeropuertos.");
            return;
        }
        for (Airport airport : found) {
            System.out.println("- " + airport.getName());
        }
    }

    private void deleteCity() {
        City city = chooseCity();
        if (input.confirm("¿Eliminar la ciudad" + city.getName() + "?")) {
            locationService.deleteCity(city.getId());
            System.out.println("ciudad eliminada.");
        }
    }

    private void deleteAirport() {
        City city = chooseCity();
        List<Airport> airports = locationService.listAirportsByCity(city.getId());
        if (airports.isEmpty()) {
            System.out.println("Esa ciudad  no tiene aeropuertos.");
            return;
        }
        for (int i = 0; i < airports.size(); i++) {
            System.out.println((i + 1) + ". " + airports.get(i).getName());
        }
        int choice = input.readInt("Elige el aeropuerto", 1, airports.size());
        locationService.deleteAirport(airports.get(choice - 1).getId());
        System.out.println("Aeropuerto eliminado.");
    }
}



