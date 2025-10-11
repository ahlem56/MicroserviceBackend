package com.esprit.microservice.trip.repositories;
import com.esprit.microservice.trip.entities.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Integer> {
    List<Trip> findByUserId(Integer userId);
    List<Trip> findByDriverId(Integer driverId);
    List<Trip> findByVehicleId(Integer vehicleId);
    List<Trip> findByTripDateBetweenAndReminderSentFalse(LocalDateTime start, LocalDateTime end);
}