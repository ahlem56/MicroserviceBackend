package tn.esprit.carpooling.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carpool_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Carpool carpool;
}
