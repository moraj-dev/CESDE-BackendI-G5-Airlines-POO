package com.airlinespoo.reservations.repository;

import com.airlinespoo.core.repository.InMemoryRepository;
import com.airlinespoo.reservations.model.Reservation;

public class ReservationRepository extends InMemoryRepository<Reservation, Integer> {
    @Override
    protected String entityName(){
        return "reservación";
    }
}

