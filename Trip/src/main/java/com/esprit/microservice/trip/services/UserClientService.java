package com.esprit.microservice.trip.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserClientService {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public UserClientService(@Value("${user.service.url}") String userServiceUrl) {
        this.restTemplate = new RestTemplate();
        this.userServiceUrl = userServiceUrl;
    }

    public String getUserProfile(String jwtToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                userServiceUrl + "/api/profile",
                HttpMethod.GET,
                entity,
                String.class
        );
        return response.getBody();
    }

    public boolean isUserAdmin(String jwtToken) {
        String profile = getUserProfile(jwtToken);
        return profile != null && profile.contains("\"role\":\"ADMIN\"");
    }
}
