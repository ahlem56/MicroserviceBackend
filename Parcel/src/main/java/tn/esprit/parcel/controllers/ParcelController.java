package tn.esprit.parcel.controllers;

import org.springframework.beans.factory.annotation.Value;  // Importing the correct Value annotation
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import tn.esprit.parcel.entities.Parcel;
import tn.esprit.parcel.services.IParcelService;
import tn.esprit.parcel.services.UserClientService;

import java.util.List;

@RestController
@RequestMapping("/parcel")
@RefreshScope
public class ParcelController {

  private final IParcelService parcelService;
  private final UserClientService userClientService;

  @Value("${welcome.message}")
  private String welcomeMessage;  // Field injection for welcomeMessage

  // Manually inject IParcelService
  public ParcelController(IParcelService parcelService, UserClientService userClientService) {
    this.parcelService = parcelService;
      this.userClientService = userClientService;
  }

  @PostMapping("/add")
  public ResponseEntity<Parcel> addParcel(
    @AuthenticationPrincipal Jwt jwt,
    @RequestBody Parcel parcel
  ) {
    String token = jwt.getTokenValue(); // 🔥 récupère le JWT brut
    String email = jwt.getClaimAsString("email");

    // Appel du MS User pour récupérer l’ID de l’utilisateur
    try {
      String userProfile = userClientService.getUserProfile(token);
      System.out.println("✅ Profil utilisateur : " + userProfile);

      // Extraire l'ID du JSON
      Long userId = extractUserId(userProfile);

      Parcel saved = parcelService.addParcel(parcel, userId);
      return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
        .body(null);
    }
  }

  // Petit helper pour parser le JSON
  private Long extractUserId(String userJson) {
    try {
      com.fasterxml.jackson.databind.JsonNode root = new com.fasterxml.jackson.databind.ObjectMapper().readTree(userJson);
      return root.path("id").asLong();
    } catch (Exception e) {
      return null;
    }
  }


  @GetMapping("/user/{userId}")
  public ResponseEntity<List<Parcel>> getParcelsByUser(@PathVariable Long userId) {
    return ResponseEntity.ok(parcelService.getParcelsByUserId(userId));
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<Parcel> updateParcel(@PathVariable Long id, @RequestBody Parcel parcel) {
    Parcel updated = parcelService.updateParcel(id, parcel);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Void> deleteParcel(@PathVariable Long id) {
    parcelService.deleteParcel(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/get/{id}")
  public ResponseEntity<Parcel> getParcelById(@PathVariable Long id) {
    return ResponseEntity.ok(parcelService.getParcelById(id));
  }

  @GetMapping("/all")
  public ResponseEntity<List<Parcel>> getAllParcels() {
    return ResponseEntity.ok(parcelService.getAllParcels());
  }

  @GetMapping("/price/{weight}")
  public ResponseEntity<Float> getPriceByWeight(@PathVariable int weight) {
    return ResponseEntity.ok(parcelService.calculatePriceByWeight(weight));
  }

  @GetMapping("/welcome")
  public String welcome() {
    return welcomeMessage;  // Injected via @Value
  }
}
