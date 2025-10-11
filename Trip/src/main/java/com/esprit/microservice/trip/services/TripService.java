package com.esprit.microservice.trip.services;

import com.esprit.microservice.trip.entities.Trip;
import com.esprit.microservice.trip.repositories.TripRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TripService {

    private final TripRepository tripRepository;

    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    public Optional<Trip> getTripById(Integer id) {
        return tripRepository.findById(id);
    }

    public Trip createTrip(Trip trip) {
        return tripRepository.save(trip);
    }

    public Trip updateTrip(Integer id, Trip tripDetails) {
        return tripRepository.findById(id).map(trip -> {
            trip.setTripDeparture(tripDetails.getTripDeparture());
            trip.setTripDestination(tripDetails.getTripDestination());
            trip.setTripDate(tripDetails.getTripDate());
            trip.setTripDuration(tripDetails.getTripDuration());
            trip.setTripPrice(tripDetails.getTripPrice());
            trip.setTripType(tripDetails.getTripType());
            trip.setReservationStatus(tripDetails.getReservationStatus());
            trip.setLatitude(tripDetails.getLatitude());
            trip.setLongitude(tripDetails.getLongitude());
            trip.setNumberOfPassengers(tripDetails.getNumberOfPassengers());
            trip.setDriverId(tripDetails.getDriverId());
            trip.setUserId(tripDetails.getUserId());
            trip.setVehicleId(tripDetails.getVehicleId());
            return tripRepository.save(trip);
        }).orElseThrow(() -> new RuntimeException("Trip not found with id " + id));
    }

    public void deleteTrip(Integer id) {
        tripRepository.deleteById(id);
    }

    public List<Trip> getTripsByUser(Integer userId) {
        return tripRepository.findByUserId(userId);
    }

    public List<Trip> getTripsByDriver(Integer driverId) {
        return tripRepository.findByDriverId(driverId);
    }

    public List<Trip> getTripsByVehicle(Integer vehicleId) {
        return tripRepository.findByVehicleId(vehicleId);
    }

    public List<Trip> getUpcomingTripsWithoutReminder(LocalDateTime start, LocalDateTime end) {
        return tripRepository.findByTripDateBetweenAndReminderSentFalse(start, end);
    }
}