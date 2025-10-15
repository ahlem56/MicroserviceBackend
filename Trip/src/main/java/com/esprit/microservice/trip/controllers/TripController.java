package com.esprit.microservice.trip.controllers;

import com.esprit.microservice.trip.entities.Trip;
import com.esprit.microservice.trip.services.TripService;
import com.esprit.microservice.trip.services.UserClientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/trip")
public class TripController {

    private final TripService tripService;
    private final UserClientService userClientService;

    public TripController(TripService tripService, UserClientService userClientService) {
        this.tripService = tripService;
        this.userClientService = userClientService;
    }

    @GetMapping("/getAllTrips")
    public List<Trip> getAllTrips() {
        return tripService.getAllTrips();
    }

    @PostMapping("/createTrip")
    public ResponseEntity<?> createTrip(@Valid @RequestBody Trip trip,
                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {

        // 1️⃣ Get JWT token
        String jwtToken = authorizationHeader.replace("Bearer ", "");

        // 2️⃣ Call the user microservice to fetch user profile
        try {
            String userProfile = userClientService.getUserProfile(jwtToken);
            System.out.println("✅ User Profile from User Microservice: " + userProfile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("❌ Failed to contact User microservice: " + e.getMessage());
        }

        // 3️⃣ Save trip
        Trip createdTrip = tripService.createTrip(trip);
        return ResponseEntity.ok(createdTrip);
    }
}
