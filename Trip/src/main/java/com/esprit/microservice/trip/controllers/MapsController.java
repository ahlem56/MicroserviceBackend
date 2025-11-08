package com.esprit.microservice.trip.controllers;

import com.esprit.microservice.trip.services.GoogleMapsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/maps")
public class MapsController {

    private final GoogleMapsService googleMapsService;

    public MapsController(GoogleMapsService googleMapsService) {
        this.googleMapsService = googleMapsService;
    }

    @GetMapping("/directions")
    public Map<String, Object> getDirections(
            @RequestParam String origin,
            @RequestParam String destination) {

        return googleMapsService.getTripDetails(origin, destination);
    }
}
