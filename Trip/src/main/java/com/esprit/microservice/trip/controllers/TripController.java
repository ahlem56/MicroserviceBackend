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

    // 👁️ Accessible à ADMIN ou USER
    @GetMapping("/getAllTrips")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<Trip> getAllTrips() {
        return tripService.getAllTrips();
    }

    // ✈️ Accessible uniquement à USER

    @PostMapping("/createTrip")
    public ResponseEntity<?> createTrip(
            @Valid @RequestBody Trip trip,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {

        String jwtToken = authorizationHeader.replace("Bearer ", "");

        try {
            // ✅ Fetch user profile JSON from User Service (secured endpoint)
            String userProfileJson = userClientService.getUserProfile(jwtToken);
            System.out.println("✅ User Profile: " + userProfileJson);

            // ✅ Extract user ID from the profile JSON (adapt key based on your UserService response)
            Integer userId = userClientService.extractUserId(userProfileJson);
            trip.setUserId(userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("❌ Failed to get user profile: " + e.getMessage());
        }

        // ✅ Calculate trip details (duration, price)
        Map<String, Object> tripDetails = googleMapsService.getTripDetails(
                trip.getTripDeparture(), trip.getTripDestination());

        trip.setTripDuration((String) tripDetails.get("duration"));
        trip.setTripPrice(calculatePrice((String) tripDetails.get("distance")));

        // ✅ Save with assigned userId & driverId
        Trip createdTrip = tripService.createTrip(trip);
        return ResponseEntity.ok(createdTrip);
    }


    private BigDecimal calculatePrice(String distanceText) {
        double distanceKm = Double.parseDouble(distanceText.replace(" km", ""));
        return BigDecimal.valueOf(3.0 + distanceKm * 1.2);
    }

    // ✅ Accessible à ADMIN ou DRIVER
    @PutMapping("/confirmTrip/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<?> confirmTrip(@PathVariable Integer id) {
        Trip trip = tripService.getTripById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        trip.setReservationStatus(ReservationStatus.CONFIRMED);
        tripService.createTrip(trip);
        return ResponseEntity.ok(trip);
    }

    // ✅ Accessible à ADMIN ou DRIVER
    @PutMapping("/declineTrip/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<?> declineTrip(@PathVariable Integer id) {
        Trip trip = tripService.getTripById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        trip.setReservationStatus(ReservationStatus.CANCELLED);
        tripService.createTrip(trip);
        return ResponseEntity.ok(trip);
    }


    // ✅ Get trips for a specific driver
    @GetMapping("/getTripsByDriver/{driverId}")
   // @PreAuthorize("hasAnyRole('DRIVER','ADMIN')")
    public ResponseEntity<List<Trip>> getTripsByDriver(@PathVariable Integer driverId) {
        List<Trip> trips = tripService.getTripsByDriver(driverId);
        if (trips.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 if none found
        }
        return ResponseEntity.ok(trips);
    }



    // ✅ Get trips for a specific user (Trip History)
    @GetMapping("/getTripsByUser/{userId}")
// @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<Trip>> getTripsByUser(@PathVariable Integer userId) {
        List<Trip> trips = tripService.getTripsByUser(userId);
        if (trips.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(trips);
    }

}
