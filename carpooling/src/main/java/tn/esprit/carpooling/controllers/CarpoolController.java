package tn.esprit.carpooling.controllers;

import tn.esprit.carpooling.entities.Carpool;
import tn.esprit.carpooling.services.CarpoolService;
import tn.esprit.carpooling.services.UserClientService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carpools")
public class CarpoolController {

    private final CarpoolService carpoolService;
    private final UserClientService userClientService;

    public CarpoolController(CarpoolService carpoolService, UserClientService userClientService) {
        this.carpoolService = carpoolService;
        this.userClientService = userClientService;
    }

    @GetMapping
    public List<Carpool> getAllCarpools() {
        return carpoolService.getAllCarpools();
    }

    @GetMapping("/destination/{destination}")
    public List<Carpool> getByDestination(@PathVariable String destination) {
        return carpoolService.findByDestination(destination);
    }

    @PostMapping
    public ResponseEntity<?> addCarpool(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Carpool carpool) {
        String token = authHeader.replace("Bearer ", "");

        // ✅ Only DRIVER or ADMIN can create carpool
        if (!userClientService.isUserDriver(token) && !userClientService.isUserAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not authorized to create a carpool.");
        }

        Carpool saved = carpoolService.addCarpool(carpool);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCarpool(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");

        if (!userClientService.isUserAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only ADMIN can delete carpools.");
        }

        carpoolService.deleteCarpool(id);
        return ResponseEntity.ok("Carpool deleted successfully.");
    }
}
