package com.airlinespoo.reservations.service;

import com.airlinespoo.core.exception.NotFoundException;
import com.airlinespoo.reservations.model.Reservation;
import com.airlinespoo.reservations.model.ReservationDetail;
import com.airlinespoo.reservations.model.Seat;
import com.airlinespoo.reservations.repository.ReservationRepository;

import java.util.List;
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

    public void addPassenger(int reservationId, ReservationDetail detail){
       Reservation reservation = getById(reservationId);
       reservation.addDetail(detail);

    }

    public void removePassenger(int reservationId, ReservationDetail detail){
        Reservation reservation = getById(reservationId);
        reservation.removeDetail(detail);
    }

    public void changeSeat(int reservationId, ReservationDetail detail, Seat newSeat){
        Reservation reservation = getById(reservationId);
        reservation.changeSeat(detail, newSeat);
    }

    public void updatePassenger(int reservationId, ReservationDetail detail, String newName, String newDocument){
        Reservation reservation = getById(reservationId);
        reservation.updatePassenger(detail, newName, newDocument);
    }

    public List<ReservationDetail> listPassengers(int reservationId){
        Reservation reservation = getById(reservationId);
        return reservation.getDetails();
    }

    public List<Reservation> listReservations(){
        return repository.findAll();
    }

    public void cancelReservation(int reservationId){
        if (!repository.deleteById(reservationId)){
            throw new NotFoundException("reservación", reservationId);
        }
    }
}
