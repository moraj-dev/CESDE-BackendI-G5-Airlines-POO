package com.airlinespoo.flights.ui;

import com.airlinespoo.core.exception.AirlinesException;
import com.airlinespoo.core.exception.ValidationException;
import com.airlinespoo.core.ui.ConsoleInput;
import com.airlinespoo.core.ui.Menu;
import com.airlinespoo.flights.model.Flight;
import com.airlinespoo.flights.model.FlightReportRow;
import com.airlinespoo.flights.service.FlightService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class FlightMenu implements Menu {

    // Mismo estilo que el formato de entrada (AAAA-MM-DD) para que el usuario vea lo mismo que escribe
    private static final DateTimeFormatter DATE_TIME_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final FlightService flightService;
    private final LocationService locationService;
    private final AirlineService airlineService;
    private final ConsoleInput input;

    public FlightMenu(FlightService flightService, LocationService locationService,
                      AirlineService airlineService, ConsoleInput input) {
        this.flightService = flightService;
        this.locationService = locationService;
        this.airlineService = airlineService;
        this.input = input;
    }

    @Override
    public String title() {
        return "Vuelos";
    }

    @Override
    public void run() {
        while (true) {
            showOptions();
            int option = input.readInt("Elige una opción", 0, 8);
            if (option == 0) {
                return;
            }
            try {
                execute(option);
            } catch (AirlinesException e) {
                System.out.println("Error: " + e.getMessage());
            }
            input.pause();
        }
    }

    private void execute(int option) {
        switch (option) {
            case 1 -> scheduleFlight();
            case 2 -> findByCode();
            case 3 -> listAll();
            case 4 -> updateFlight();
            case 5 -> cancelFlight();
            case 6 -> listByAirline();
            case 7 -> listByDateRange();
            case 8 -> monthlyReport();
        }
    }

    private void showOptions() {
        System.out.println("""

                === VUELOS ===
                1. Programar vuelo
                2. Consultar vuelo por código
                3. Listar vuelos
                4. Reprogramar o actualizar vuelo
                5. Cancelar vuelo
                6. Listar vuelos por aerolínea
                7. Listar vuelos por rango de fechas
                8. Reporte mensual por aerolínea
                0. Volver
                """);
    }

    // ---------- Opciones ----------

    private void scheduleFlight() {
        FlightData data = askFlightData();
        Flight flight = flightService.createFlight(data.code(), data.departure(), data.arrival(),
            data.depDate(), data.depTime(), data.arrDate(), data.arrTime(), data.airline());
        System.out.println("Vuelo programado:");
        printFlight(flight);
    }

    private void findByCode() {
        String code = input.readRequiredText("Código del vuelo");
        printFlight(flightService.getFlightByName(code));
    }

    private void listAll() {
        printFlights(flightService.listFlights());
    }

    private void updateFlight() {
        Flight current = flightService.getFlightByName(
            input.readRequiredText("Código del vuelo a actualizar"));
        System.out.println("Datos actuales:");
        printFlight(current);

        System.out.println("Ingresa los datos nuevos:");
        FlightData data = askFlightData();
        Flight updated = flightService.updateFlight(current.getId(), data.code(),
            data.departure(), data.arrival(), data.depDate(), data.depTime(),
            data.arrDate(), data.arrTime(), data.airline());
        System.out.println("Vuelo actualizado:");
        printFlight(updated);
    }

    private void cancelFlight() {
        Flight flight = flightService.getFlightByName(
            input.readRequiredText("Código del vuelo a cancelar"));
        printFlight(flight);
        if (input.confirm("¿Confirmas la cancelación?")) {
            flightService.deleteFlight(flight.getId());
            System.out.println("Vuelo cancelado.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private void listByAirline() {
        printFlights(flightService.listFlightsByAirline(chooseAirline()));
    }

    private void listByDateRange() {
        LocalDate start = input.readDate("Fecha inicial");
        LocalDate end = input.readDate("Fecha final");
        printFlights(flightService.listFlightsBetween(start, end));
    }

    private void monthlyReport() {
        int year = input.readInt("Año", 1900, 2100);
        int month = input.readInt("Mes", 1, 12);
        Map<Airline, List<FlightReportRow>> report =
            flightService.listFlightsByAirlineInMonth(year, month);

        if (report.isEmpty()) {
            System.out.println("Sin vuelos en ese periodo.");
            return;
        }
        for (Map.Entry<Airline, List<FlightReportRow>> entry : report.entrySet()) {
            System.out.println("\n--- " + entry.getKey().getName() + " ---");
            for (FlightReportRow row : entry.getValue()) {
                printReportRow(row);
            }
        }
    }

    private void printReportRow(FlightReportRow row) {
        System.out.println("Vuelo " + row.flightName());
        System.out.println("  Salida:  " + row.departureCity() + " - " + row.departureAirport()
            + " | " + row.departureDate() + " " + row.departureTime());
        System.out.println("  Llegada: " + row.arrivalCity() + " - " + row.arrivalAirport()
            + " | " + row.arrivalDate() + " " + row.arrivalTime());
    }

    // ---------- Lectura de datos ----------

    // Agrupa los 8 datos que piden tanto "programar" como "actualizar" (evita repetir código)
    private record FlightData(String code, Airport departure, Airport arrival,
                              LocalDate depDate, LocalTime depTime,
                              LocalDate arrDate, LocalTime arrTime, Airline airline) { }

    private FlightData askFlightData() {
        String code = input.readRequiredText("Código del vuelo (máx. 8, letras y dígitos)");
        Airport departure = chooseAirport("Aeropuerto de salida");
        Airport arrival = chooseAirport("Aeropuerto de llegada");
        LocalDate depDate = input.readDate("Fecha de salida");
        LocalTime depTime = input.readTime("Hora de salida");
        LocalDate arrDate = input.readDate("Fecha de llegada");
        LocalTime arrTime = input.readTime("Hora de llegada");
        Airline airline = chooseAirline();
        return new FlightData(code, departure, arrival, depDate, depTime, arrDate, arrTime, airline);
    }

    private Airport chooseAirport(String label) {
        List<Airport> airports = locationService.listAirports();
        if (airports.isEmpty()) {
            throw new ValidationException("No hay aeropuertos registrados.");
        }
        System.out.println(label + ":");
        for (int i = 0; i < airports.size(); i++) {
            System.out.println((i + 1) + ". " + airports.get(i).getName());
        }
        int number = input.readInt("Número", 1, airports.size());
        return airports.get(number - 1);
    }

    private Airline chooseAirline() {
        List<Airline> airlines = airlineService.listAirlines();
        if (airlines.isEmpty()) {
            throw new ValidationException("No hay aerolíneas registradas.");
        }
        System.out.println("Aerolínea:");
        for (int i = 0; i < airlines.size(); i++) {
            System.out.println((i + 1) + ". " + airlines.get(i).getName());
        }
        int number = input.readInt("Número", 1, airlines.size());
        return airlines.get(number - 1);
    }

    // ---------- Presentación ----------

    private void printFlights(List<Flight> flights) {
        if (flights.isEmpty()) {
            System.out.println("No hay vuelos para mostrar.");
            return;
        }
        for (Flight flight : flights) {
            printFlight(flight);
            System.out.println();
        }
    }

    private void printFlight(Flight f) {
        System.out.println("Vuelo " + f.getFlightName() + " | " + f.getAirline().getName());
        System.out.println("  Salida:   " + f.getDeparture().getName()
            + " - " + f.getDepartureDateTime().format(DATE_TIME_FORMAT));
        System.out.println("  Llegada:  " + f.getArrival().getName()
            + " - " + f.getArrivalDateTime().format(DATE_TIME_FORMAT));
        System.out.println("  Duración: " + formatDuration(f.getFlightDuration()));
    }

    private String formatDuration(Duration duration) {
        return duration.toHours() + " h " + duration.toMinutesPart() + " min";
    }
}
