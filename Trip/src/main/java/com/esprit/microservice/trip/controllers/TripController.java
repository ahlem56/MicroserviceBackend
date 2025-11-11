package com.esprit.microservice.trip.controllers;

import com.esprit.microservice.trip.entities.ReservationStatus;
import com.esprit.microservice.trip.entities.Trip;
import com.esprit.microservice.trip.services.GoogleMapsService;
import com.esprit.microservice.trip.services.TripService;
import com.esprit.microservice.trip.services.UserClientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("")
public class TripController {

    private final TripService tripService;
    private final UserClientService userClientService;
    private final GoogleMapsService googleMapsService;

    public TripController(TripService tripService, UserClientService userClientService, GoogleMapsService googleMapsService) {
        this.tripService = tripService;
        this.userClientService = userClientService;
        this.googleMapsService = googleMapsService;
    }

    @GetMapping("/getAllTrips")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<Trip> getAllTrips() {
        return tripService.getAllTrips();
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER','DRIVER')")
    public ResponseEntity<?> getTripById(@PathVariable Integer id) {
        return tripService.getTripById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Trip not found with id " + id));
    }


    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<?> updateTrip(
            @PathVariable Integer id,
            @Valid @RequestBody Trip tripDetails) {

        try {
            Trip updated = tripService.updateTrip(id, tripDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<?> deleteTrip(@PathVariable Integer id) {
        try {
            tripService.deleteTrip(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Trip not found with id " + id);
        }
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/createTrip")
    public ResponseEntity<?> createTrip(
            @Valid @RequestBody Trip trip,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {

        String jwtToken = authorizationHeader.replace("Bearer ", "");

        try {
            String userProfileJson = userClientService.getUserProfile(jwtToken);
            System.out.println("✅ User Profile: " + userProfileJson);

            Integer userId = userClientService.extractUserId(userProfileJson);
            trip.setUserId(userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("❌ Failed to get user profile: " + e.getMessage());
        }

        Map<String, Object> tripDetails = googleMapsService.getTripDetails(
                trip.getTripDeparture(), trip.getTripDestination());

        trip.setTripDuration((String) tripDetails.get("duration"));
        trip.setTripPrice(calculatePrice((String) tripDetails.get("distance")));

        Trip createdTrip = tripService.createTrip(trip);
        return ResponseEntity.ok(createdTrip);
    }


    private BigDecimal calculatePrice(String distanceText) {
        double distanceKm = Double.parseDouble(distanceText.replace(" km", ""));
        return BigDecimal.valueOf(3.0 + distanceKm * 1.2);
    }

    @PutMapping("/confirmTrip/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<Trip> confirmTrip(@PathVariable Integer id) {
        return tripService.getTripById(id)
                .map(trip -> {
                    trip.setReservationStatus(ReservationStatus.CONFIRMED);
                    Trip updated = tripService.createTrip(trip);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }



    @PutMapping("/cancelTrip/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'USER')")
    public ResponseEntity<Trip> cancelTrip(@PathVariable Integer id) {
        try {
            Trip updated = tripService.updateTripStatus(id, ReservationStatus.CANCELLED);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @PutMapping("/completeTrip/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DRIVER')")
    public ResponseEntity<Trip> completeTrip(@PathVariable Integer id) {
        return tripService.getTripById(id)
                .map(trip -> {
                    trip.setReservationStatus(ReservationStatus.COMPLETED);
                    trip.setReadyForDriverRating(true);
                    trip.setReadyForPassengerRating(true);
                    Trip updated = tripService.createTrip(trip);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }



    @PutMapping("/declineTrip/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<?> declineTrip(@PathVariable Integer id) {
        Trip trip = tripService.getTripById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        trip.setReservationStatus(ReservationStatus.CANCELLED);
        tripService.createTrip(trip);
        return ResponseEntity.ok(trip);
    }


    @GetMapping("/getTripsByDriver/{driverId}")
   @PreAuthorize("hasAnyRole('DRIVER','ADMIN')")
    public ResponseEntity<List<Trip>> getTripsByDriver(@PathVariable Integer driverId) {
        List<Trip> trips = tripService.getTripsByDriver(driverId);
        if (trips.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 if none found
        }
        return ResponseEntity.ok(trips);
    }



    @GetMapping("/getTripsByUser/{userId}")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<Trip>> getTripsByUser(@PathVariable Integer userId) {
        List<Trip> trips = tripService.getTripsByUser(userId);
        if (trips.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(trips);
    }

}
