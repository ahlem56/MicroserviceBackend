package com.esprit.microservice.trip.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service", url = "http://speedygo-user-service:8084")
public interface UserClient {

    @GetMapping("/api/profile")
    String getUserProfile(@RequestHeader("Authorization") String token);
}
