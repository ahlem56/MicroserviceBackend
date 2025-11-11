package org.event.demo.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer vehiculeId;

    private Integer driverId;          // ID du chauffeur
    private String model;              // Modèle du véhicule
    private Integer capacity;          // Capacité (ex: nombre de places)
    private Integer serialNumber;      // Numéro de série unique
    private LocalDate maintenanceDate; // Date de dernière maintenance
    private String insuranceStatus;    // Statut de l’assurance (valide, expirée, etc.)
}

