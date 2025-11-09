package tn.esprit.carpooling.controllers;

import tn.esprit.carpooling.entities.Carpool;
import tn.esprit.carpooling.services.CarpoolService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carpools")
public class CarpoolController {

    private final CarpoolService carpoolService;

    public CarpoolController(CarpoolService carpoolService) {
        this.carpoolService = carpoolService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','DRIVER','ADMIN')")
    public List<Carpool> getAllCarpools() {
        return carpoolService.getAllCarpools();
    }

    @GetMapping("/destination/{destination}")
    @PreAuthorize("hasAnyRole('USER','DRIVER','ADMIN')")
    public List<Carpool> getByDestination(@PathVariable String destination) {
        return carpoolService.findByDestination(destination);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DRIVER','ADMIN')")
    public ResponseEntity<Carpool> addCarpool(@RequestBody Carpool carpool) {
        Carpool saved = carpoolService.addCarpool(carpool);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DRIVER','ADMIN')")
    public ResponseEntity<String> deleteCarpool(@PathVariable Long id) {
        carpoolService.deleteCarpool(id);
        return ResponseEntity.ok("Carpool deleted successfully");
    }
}
