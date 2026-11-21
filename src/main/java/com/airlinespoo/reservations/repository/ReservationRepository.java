package com.airlinespoo.reservations.repository;

import com.airlinespoo.core.repository.InMemoryRepository;
import com.airlinespoo.reservations.model.Reservation;

import java.util.ArrayList;
import java.util.List;

public class ReservationRepository extends InMemoryRepository<Reservation, Integer> {
    @Override
    protected String entityName() {
        return "reservación";
    }

    public List<Reservation> findByCustomer(String customerId) {
        List<Reservation> found = new ArrayList<>();
        for (Reservation reservation : values()) {
            if (reservation.getCustomer().getId().equals(customerId)) {
                found.add(reservation);

            }
        }

        return found;
    }
}

