package com.airlinespoo.locations.ui;

import com.airlinespoo.locations.model.City;
import com.airlinespoo.locations.service.LocationService;

public class LocationSampleData {

    private LocationSampleData() {
    }

    public static void load(LocationService service) {
        City cali = service.createCity("Cali");
        City pereira = service.createCity("Pereira");
        City medellin = service.createCity("Medellin");
        City bogota = service.createCity("Bogota");
        City santander = service.createCity("Santander");

        service.createAirport("Aeropuerto internacional Alfonso Bonilla Aragon", cali.getId());
        service.createAirport("Aeropuerto internacional Matecaña", pereira.getId());
        service.createAirport("Aeropuerto internacional Jose Maria Cordova", medellin.getId());
        service.createAirport("Aeropuerto Olaya Herrera", medellin.getId());
        service.createAirport("Aeropuerto internacional el Dorado", bogota.getId());
        service.createAirport("Aeropuerto Seve Ballesteros- Santander", santander.getId());
    }
}
