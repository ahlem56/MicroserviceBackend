package com.esprit.microservice.trip.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoogleMapsService {
    @Value("${google.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> getTripDetails(String origin, String destination) {
        String url = String.format(
                "https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&key=%s",
                origin, destination, apiKey
        );

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map<String, Object> data = response.getBody();

        Map<String, Object> result = new HashMap<>();
        if (data != null && data.containsKey("routes")) {
            var routes = (List<Map<String, Object>>) data.get("routes");
            if (!routes.isEmpty()) {
                var route = routes.get(0);
                var legs = (List<Map<String, Object>>) route.get("legs");
                if (!legs.isEmpty()) {
                    var leg = legs.get(0);
                    result.put("distance", ((Map) leg.get("distance")).get("text"));
                    result.put("duration", ((Map) leg.get("duration")).get("text"));
                }
                // ✅ expose encoded polyline so Angular can draw it
                Map<String, Object> overviewPolyline = (Map<String, Object>) route.get("overview_polyline");
                if (overviewPolyline != null) {
                    result.put("polyline", overviewPolyline.get("points"));
                }
            }
        }

        System.out.println("🌍 Google API response: " + data);
        return result;
    }
}
