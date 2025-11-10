package com.esprit.microservice.trip.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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


    // ✅ Extract userId (or username) from profile JSON
    public Integer extractUserId(String userProfileJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(userProfileJson);

            if (root.has("id")) {
                JsonNode idNode = root.get("id");
                if (idNode.isInt()) {
                    return idNode.asInt();
                } else if (idNode.isTextual()) {
                    return Integer.parseInt(idNode.asText());
                }
            } else if (root.has("userId")) {
                JsonNode idNode = root.get("userId");
                if (idNode.isInt()) {
                    return idNode.asInt();
                } else if (idNode.isTextual()) {
                    return Integer.parseInt(idNode.asText());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
