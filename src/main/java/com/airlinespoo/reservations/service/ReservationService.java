package com.airlinespoo.reservations.service;

import com.airlinespoo.core.exception.NotFoundException;
import com.airlinespoo.reservations.model.Reservation;
import com.airlinespoo.reservations.repository.ReservationRepository;

import java.util.Optional;

public class ReservationService {
    private final ReservationRepository repository;

    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    public Reservation getById(int id) {
        Optional<Reservation> found = repository.findById(id);
        if (found.isEmpty()) {
            throw new NotFoundException("reservación", id);
        }
        return found.get();
    }
}
