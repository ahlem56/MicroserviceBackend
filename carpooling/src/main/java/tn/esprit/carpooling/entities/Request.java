package tn.esprit.carpooling.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long passengerId;       // comes from User Service
    private String requestedDestination;

    @ManyToOne
    private Carpool carpool;
}
