package com.airlinespoo.reservations.ui;

import com.airlinespoo.core.ui.ConsoleInput;
import com.airlinespoo.core.ui.Menu;
import com.airlinespoo.reservations.service.ReservationService;

public class ReservationMenu implements Menu {
    private final ConsoleInput input;
    private final ReservationService service;
    public String title(){
        return "Reservas";
    }

    public ReservationMenu(ConsoleInput input, ReservationService service) {
        this.service = service;
        this.input = input;
    }

    public void run(){

        while (true){
            System.out.println("===RESERVAS===");
            System.out.println("1. Agregar pasajero");
            System.out.println("2. Actualizar datos del pasajero");
            System.out.println("3. Cambiar silla del pasajero");
            System.out.println("4. Eliminar pasajero");
            System.out.println("5. Listar pasajeros");
            System.out.println("6. Listar reservaciones");
            System.out.println("7. Cancelar reservación");
            System.out.println("8. Volver");
            int option = input.readInt("Elige una opción", 1, 8);
        if (option == 8){
            return;
        }
        }

    }

}
