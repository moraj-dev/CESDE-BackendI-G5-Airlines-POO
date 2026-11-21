package com.airlinespoo.customers.ui;

import com.airlinespoo.customers.service.CustomerService;

/**
 * Datos de ejemplo del módulo 1: 5 países, 4 tipos de documento y 3 clientes.
 * Se cargan al iniciar el programa para que los demas módulos puedan probar sin escribir datos cada vez.
 * Se crean por medio del servicio, así pasan por las mismas validaciones que los datos del usuario
 */
public final class CustomerSampleData {

    //Constructor privado: de esta clase no se crean objetos, solo se usa el metodo load
    private CustomerSampleData() {
    }

    /**
     * Carga todos los datos de ejemplo. Primero países y tipos de documento porque el cliente los necesita
     */
    public static void load(CustomerService service) {
        loadCountries(service);
        loadDocumentTypes(service);
        loadCustomers(service);
    }

    //Los 5 países (id, nombre)
    private static void loadCountries(CustomerService service) {
        service.createCountry(1, "Colombia");
        service.createCountry(2, "Estados Unidos");
        service.createCountry(3, "México");
        service.createCountry(4, "España");
        service.createCountry(5, "Argentina");
    }

    //Los 4 tipos de documento (código de 2 letras, descripción)
    private static void loadDocumentTypes(CustomerService service) {
        service.createDocumentType("CC", "Cédula de ciudadanía");
        service.createDocumentType("TI", "Tarjeta de identidad");
        service.createDocumentType("CE", "Cédula extranjería");
        service.createDocumentType("PA", "Pasaporte");
    }

    //Los 3 clientes. Los apellidos estan desordenados a proposito para comprobar que el listado los ordena
    //Carlos no tiene dirección y Lucía no tiene correo (null) porque son datos opcionales
    private static void loadCustomers(CustomerService service) {
        service.createCustomer("1001234567", "Ana", "Pérez", "CC", "3001234567",
            "ana.perez@mail.com", 1, "Calle 10 # 20-30, Medellín");
        service.createCustomer("98765432", "Carlos", "Gómez", "CC", "+573109876543",
            "carlos.gomez@mail.com", 1, null);
        service.createCustomer("AB123456", "Lucía", "Arango", "PA", "+525512345678",
            null, 3, "Av. Reforma 100, CDMX");
    }
}
