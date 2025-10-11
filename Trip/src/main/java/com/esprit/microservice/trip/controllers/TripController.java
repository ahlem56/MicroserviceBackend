package com.esprit.microservice.trip.controllers;


import com.esprit.microservice.trip.entities.Trip;
import com.esprit.microservice.trip.services.TripService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trip")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping("/getAllTrips")
    public List<Trip> getAllTrips() {
        return tripService.getAllTrips();
    }

    @GetMapping("/getTrip/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable Integer id) {
        return tripService.getTripById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/createTrip")
    public ResponseEntity<Trip> createTrip(@Valid @RequestBody Trip trip) {
        return ResponseEntity.ok(tripService.createTrip(trip));
    }

    @PutMapping("/updateTrip/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable Integer id, @Valid @RequestBody Trip tripDetails) {
        return ResponseEntity.ok(tripService.updateTrip(id, tripDetails));
    }

    @DeleteMapping("/deleteTrip/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Integer id) {
        tripService.deleteTrip(id);
        return ResponseEntity.noContent().build();
    }

    // Custom endpoints
    @GetMapping("/getTripByUser/{userId}")
    public List<Trip> getTripsByUser(@PathVariable Integer userId) {
        return tripService.getTripsByUser(userId);
    }

    @GetMapping("/getTripByDriver/{driverId}")
    public List<Trip> getTripsByDriver(@PathVariable Integer driverId) {
        return tripService.getTripsByDriver(driverId);
    }

    @GetMapping("/getTripByVehicle/{vehicleId}")
    public List<Trip> getTripsByVehicle(@PathVariable Integer vehicleId) {
        return tripService.getTripsByVehicle(vehicleId);
    }
}