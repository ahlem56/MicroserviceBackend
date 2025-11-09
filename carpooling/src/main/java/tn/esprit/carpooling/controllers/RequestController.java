package tn.esprit.carpooling.controllers;

import tn.esprit.carpooling.entities.Request;
import tn.esprit.carpooling.services.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('USER','DRIVER','ADMIN')")
    public ResponseEntity<Request> addRequest(@RequestBody Request request) {
        Request saved = requestService.addRequest(request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','DRIVER','ADMIN')")
    public List<Request> getAllRequests() {
        return requestService.getAllRequests();
    }
}
