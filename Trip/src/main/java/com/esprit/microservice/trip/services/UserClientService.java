package com.esprit.microservice.trip.services;

import com.esprit.microservice.trip.clients.UserClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class UserClientService {

    private final UserClient userClient;

    public UserClientService(UserClient userClient) {
        this.userClient = userClient;
    }

    public String getUserProfile(String jwtToken) {
        return userClient.getUserProfile("Bearer " + jwtToken);
    }

    public Integer extractUserId(String userProfileJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(userProfileJson);
            if (root.has("id")) return root.get("id").asInt();
            if (root.has("userId")) return root.get("userId").asInt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
