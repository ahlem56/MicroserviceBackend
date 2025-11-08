package tn.esprit.carpooling.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Carpool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long driverId;          // comes from User Service
    private String destination;
    private int availableSeats;
    private double price;

    @ElementCollection
    private List<String> suggestions;
}
