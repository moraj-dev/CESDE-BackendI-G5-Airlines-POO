package com.airlinespoo.reservations.service;

import com.airlinespoo.core.exception.NotFoundException;
import com.airlinespoo.core.util.IdGenerator;
import com.airlinespoo.reservations.model.Reservation;
import com.airlinespoo.reservations.model.ReservationDetail;
import com.airlinespoo.reservations.model.Seat;
import com.airlinespoo.reservations.repository.ReservationRepository;

import java.util.List;
import java.util.Optional;

public class ReservationService {
    private final ReservationRepository repository;
    private final IdGenerator detailIdGenerator;

    public ReservationService(ReservationRepository repository, IdGenerator detailIdGenerator) {
        this.repository = repository;
        this.detailIdGenerator = detailIdGenerator;
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

    public void addPassenger(int reservationId, String passengerFullName, String passengerDocument, Seat seat){
        ReservationDetail detail2 = new ReservationDetail(detailIdGenerator.next(), passengerFullName, passengerDocument, seat);
        addPassenger(reservationId,detail2);
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

    public ReservationDetail findPassenger(int reservationId, int passengerId){
        Reservation reservation = getById(reservationId);
        for (ReservationDetail detail : reservation.getDetails()){
            if (detail.getId() == passengerId){
                return detail;
            }
        }
        throw new NotFoundException("pasajero", passengerId);
    }

    public List<ReservationDetail> listPassengers(int reservationId){
        Reservation reservation = getById(reservationId);
        return reservation.getDetails();
    }

    public List<Reservation> listReservations(){
        return repository.findAll();
    }

    public List<Reservation> listReservationsByCustomer(String customerId){
        return repository.findByCustomer(customerId);
    }

    public boolean customerHasReservations(String customerId){
        return repository.existsByCustomer(customerId);
    }

    public void cancelReservation(int reservationId){
        if (!repository.deleteById(reservationId)){
            throw new NotFoundException("reservación", reservationId);
        }
    }
}
