package com.airlinespoo.customers.ui;

import com.airlinespoo.core.exception.AirlinesException;
import com.airlinespoo.core.ui.ConsoleInput;
import com.airlinespoo.core.ui.Menu;
import com.airlinespoo.customers.model.Country;
import com.airlinespoo.customers.model.Customer;
import com.airlinespoo.customers.model.DocumentType;
import com.airlinespoo.customers.service.CustomerService;

import java.util.List;

//Menú de consola del módulo 1. Esta es la única capa que habla con el usuario:
//lee los datos con ConsoleInput, llama al servicio y muestra el resultado.
//Si el servicio lanza una excepción se muestra el mensaje y el programa sigue, nunca se cierra

public class CustomerMenu implements Menu {

    //Textos que se repiten varias veces cuando se piden datos
    private static final String DOCUMENT_PROMPT = "Número de documento";
    private static final String COUNTRY_ID_PROMPT = "Id del país";
    private static final String TYPE_CODE_PROMPT = "Código del tipo de documento";

    private final CustomerService service;
    private final ConsoleInput input;

    //Creación del menú con el servicio y el lector de consola que se van a usar
    public CustomerMenu(CustomerService service, ConsoleInput input) {
        this.service = service;
        this.input = input;
    }

    //Titulo que muestra el menú principal
    @Override
    public String title() {
        return "Clientes y catálogos";
    }

    //Muestra las opciones y se repite hasta que el usuario escriba 0
    //El try catch atrapa los errores del servicio para mostrar el mensaje y que el menú no se cierre
    @Override
    public void run() {
        int option;
        do {
            printOptions();
            option = input.readInt("Opción", 0, 7);
            try {
                handle(option);
            } catch (AirlinesException e) {
                showError(e);
            }
        } while (option != 0);
    }

    //Imprime las opciones del menú de clientes
    private void printOptions() {
        System.out.println();
        System.out.println("=== Clientes y catálogos ===");
        System.out.println("1. Registrar cliente");
        System.out.println("2. Consultar cliente por documento");
        System.out.println("3. Actualizar datos de contacto");
        System.out.println("4. Listar clientes");
        System.out.println("5. Buscar clientes por apellido");
        System.out.println("6. Eliminar cliente");
        System.out.println("7. Catálogos: países y tipos de documento");
        System.out.println("0. Volver");
    }

    //Segun el numero que escribe el usuario se llama al metodo de esa opción
    private void handle(int option) {
        switch (option) {
            case 1 -> registerCustomer();
            case 2 -> findCustomer();
            case 3 -> updateContact();
            case 4 -> listCustomers();
            case 5 -> searchByLastName();
            case 6 -> deleteCustomer();
            case 7 -> runCatalogs();
            default -> System.out.println("Volviendo al menú anterior.");
        }
    }

    //Muestra el mensaje del error que lanzó el servicio
    private static void showError(AirlinesException e) {
        System.out.println("No se pudo completar la operación: " + e.getMessage());
    }

    //-----------------------------------------------------------//--------------------------------------------------//
    //CLIENTES: en cada opción primero se piden los datos y despues se llama al servicio

    //Opción 1: pide todos los datos del cliente y lo registra
    //Se muestran los tipos de documento y los países para que el usuario sepa cuales existen
    private void registerCustomer() {
        System.out.println("--- Registrar cliente ---");
        printDocumentTypes();
        String documentTypeCode = input.readRequiredText(TYPE_CODE_PROMPT);
        String document = input.readRequiredText(DOCUMENT_PROMPT);
        String firstName = input.readRequiredText("Nombres");
        String lastName = input.readRequiredText("Apellidos");
        String telephone = input.readRequiredText("Teléfono (solo dígitos, + opcional al inicio)");
        String email = input.readOptionalText("Correo electrónico"); //Opcional
        printCountries();
        int countryId = input.readInt(COUNTRY_ID_PROMPT, 1, 195);
        String address = input.readOptionalText("Dirección"); //Opcional

        Customer customer = service.createCustomer(document, firstName, lastName, documentTypeCode,
            telephone, email, countryId, address);
        System.out.println("Cliente registrado: " + customer.getFullName() + " (" + customer.getId() + ").");
    }

    //Opción 2: busca un cliente por su documento y muestra sus datos
    private void findCustomer() {
        System.out.println("--- Consultar cliente ---");
        String document = input.readRequiredText(DOCUMENT_PROMPT);

        printCustomerDetails(service.getCustomer(document));
    }

    //Opción 3: cambia el telefono, el correo y la dirección de un cliente
    //Primero muestra los datos que tiene y luego pide los nuevos
    private void updateContact() {
        System.out.println("--- Actualizar datos de contacto ---");
        String document = input.readRequiredText(DOCUMENT_PROMPT);
        printCustomerDetails(service.getCustomer(document));
        System.out.println("Escribe los datos nuevos (en los opcionales, vacío = sin dato).");
        String telephone = input.readRequiredText("Teléfono");
        String email = input.readOptionalText("Correo electrónico");
        String address = input.readOptionalText("Dirección");

        Customer updated = service.updateCustomer(document, telephone, email, address);
        System.out.println("Datos de contacto actualizados.");
        printCustomerDetails(updated);
    }

    //Opción 4: lista todos los clientes (el servicio ya los entrega ordenados) y el total
    private void listCustomers() {
        System.out.println("--- Clientes (ordenados por apellido y nombre) ---");
        printCustomers(service.listCustomers());
        System.out.println("Total: " + service.countCustomers());
    }

    //Opción 5: busca clientes por una parte del apellido
    private void searchByLastName() {
        System.out.println("--- Buscar por apellido ---");
        String text = input.readRequiredText("Apellido o parte del apellido");

        printCustomers(service.searchCustomersByLastName(text));
    }

    //Opción 6: elimina un cliente, pero antes muestra sus datos y pide confirmación
    private void deleteCustomer() {
        System.out.println("--- Eliminar cliente ---");
        String document = input.readRequiredText(DOCUMENT_PROMPT);
        printCustomerDetails(service.getCustomer(document));
        if (!input.confirm("¿Seguro que deseas eliminar este cliente?")) {
            System.out.println("Eliminación cancelada.");
            return; //Si el usuario dice que no, no se elimina nada
        }

        service.deleteCustomer(document);
        System.out.println("Cliente eliminado.");
    }

    //Imprime una lista de clientes, uno por linea: tipo y documento | apellido, nombre | país
    private void printCustomers(List<Customer> customers) {
        if (customers.isEmpty()) {
            System.out.println("No hay clientes para mostrar.");
            return;
        }
        for (Customer customer : customers) {
            System.out.println(customer.getDocumentType().getId() + " " + customer.getId()
                + " | " + customer.getLastName() + ", " + customer.getFirstName()
                + " | " + customer.getCountry().getName());
        }
    }

    //Imprime todos los datos de un solo cliente
    private void printCustomerDetails(Customer customer) {
        System.out.println("Documento : " + customer.getDocumentType().getId() + " " + customer.getId());
        System.out.println("Nombre    : " + customer.getFullName());
        System.out.println("Teléfono  : " + customer.getTelephoneNumber());
        System.out.println("Correo    : " + orNoData(customer.getEmail()));
        System.out.println("País      : " + customer.getCountry().getName());
        System.out.println("Dirección : " + orNoData(customer.getAddress()));
    }

    //El correo y la dirección son opcionales: si no hay dato (null) se muestra "(sin dato)"
    private static String orNoData(String value) {
        if (value == null) {
            return "(sin dato)";
        }
        return value;
    }

    //-----------------------------------------------------------//--------------------------------------------------//
    //CATALOGOS: submenú de países y tipos de documento, funciona igual que el menú de clientes

    //Opción 7: muestra el submenú y se repite hasta que el usuario escriba 0
    private void runCatalogs() {
        int option;
        do {
            printCatalogOptions();
            option = input.readInt("Opción", 0, 8);
            try {
                handleCatalog(option);
            } catch (AirlinesException e) {
                showError(e);
            }
        } while (option != 0);
    }

    //Imprime las opciones del submenú
    private void printCatalogOptions() {
        System.out.println();
        System.out.println("=== Países y tipos de documento ===");
        System.out.println("1. Registrar país");
        System.out.println("2. Listar países");
        System.out.println("3. Cambiar nombre de un país");
        System.out.println("4. Eliminar país");
        System.out.println("5. Registrar tipo de documento");
        System.out.println("6. Listar tipos de documento");
        System.out.println("7. Cambiar descripción de un tipo de documento");
        System.out.println("8. Eliminar tipo de documento");
        System.out.println("0. Volver");
    }

    //Segun el numero que escribe el usuario se llama al metodo de esa opción del submenú
    private void handleCatalog(int option) {
        switch (option) {
            case 1 -> createCountry();
            case 2 -> printCountries();
            case 3 -> renameCountry();
            case 4 -> deleteCountry();
            case 5 -> createDocumentType();
            case 6 -> printDocumentTypes();
            case 7 -> updateDocumentType();
            case 8 -> deleteDocumentType();
            default -> System.out.println("Volviendo al menú de clientes.");
        }
    }

    //Registra un país (Create)
    private void createCountry() {
        System.out.println("--- Registrar país ---");
        int id = input.readInt(COUNTRY_ID_PROMPT, 1, 195);
        String name = input.readRequiredText("Nombre del país");

        Country country = service.createCountry(id, name);
        System.out.println("País registrado: " + country.getId() + " - " + country.getName());
    }

    //Cambia el nombre de un país (Update)
    private void renameCountry() {
        System.out.println("--- Cambiar nombre de un país ---");
        int id = input.readInt(COUNTRY_ID_PROMPT, 1, 195);
        String name = input.readRequiredText("Nombre nuevo");

        Country country = service.updateCountry(id, name);
        System.out.println("País actualizado: " + country.getId() + " - " + country.getName());
    }

    //Elimina un país (Delete), el servicio lo rechaza si tiene clientes
    private void deleteCountry() {
        System.out.println("--- Eliminar país ---");
        int id = input.readInt(COUNTRY_ID_PROMPT, 1, 195);

        service.deleteCountry(id);
        System.out.println("País eliminado.");
    }

    //Registra un tipo de documento (Create)
    private void createDocumentType() {
        System.out.println("--- Registrar tipo de documento ---");
        String code = input.readRequiredText("Código (2 caracteres, por ejemplo CC)");
        String description = input.readRequiredText("Descripción");

        DocumentType documentType = service.createDocumentType(code, description);
        System.out.println("Tipo de documento registrado: " + documentType.getId() + " - "
            + documentType.getDescription());
    }

    //Cambia la descripción de un tipo de documento (Update)
    private void updateDocumentType() {
        System.out.println("--- Cambiar descripción de un tipo de documento ---");
        String code = input.readRequiredText(TYPE_CODE_PROMPT);
        String description = input.readRequiredText("Descripción nueva");

        DocumentType documentType = service.updateDocumentType(code, description);
        System.out.println("Tipo de documento actualizado: " + documentType.getId() + " - "
            + documentType.getDescription());
    }

    //Elimina un tipo de documento (Delete), el servicio lo rechaza si algun cliente lo usa
    private void deleteDocumentType() {
        System.out.println("--- Eliminar tipo de documento ---");
        String code = input.readRequiredText(TYPE_CODE_PROMPT);

        service.deleteDocumentType(code);
        System.out.println("Tipo de documento eliminado.");
    }

    //Imprime los países ordenados por nombre, tambien se usa al registrar un cliente
    private void printCountries() {
        List<Country> countries = service.listCountries();
        if (countries.isEmpty()) {
            System.out.println("No hay países registrados.");
            return;
        }
        System.out.println("Países (ordenados por nombre):");
        for (Country country : countries) {
            System.out.println("  " + country.getId() + " - " + country.getName());
        }
    }

    //Imprime los tipos de documento, tambien se usa al registrar un cliente
    private void printDocumentTypes() {
        List<DocumentType> documentTypes = service.listDocumentTypes();
        if (documentTypes.isEmpty()) {
            System.out.println("No hay tipos de documento registrados.");
            return;
        }
        System.out.println("Tipos de documento:");
        for (DocumentType documentType : documentTypes) {
            System.out.println("  " + documentType.getId() + " - " + documentType.getDescription());
        }
    }
}
