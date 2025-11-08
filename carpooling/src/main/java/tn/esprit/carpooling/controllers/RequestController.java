package tn.esprit.carpooling.controllers;

import tn.esprit.carpooling.entities.Request;
import tn.esprit.carpooling.services.RequestService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ResponseEntity<Request> addRequest(@RequestBody Request request) {
        Request saved = requestService.addRequest(request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<Request> getAllRequests() {
        return requestService.getAllRequests();
    }
}
