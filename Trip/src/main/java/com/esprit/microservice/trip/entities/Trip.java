package com.esprit.microservice.trip.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tripId;

    @NotNull
    private String tripDeparture;

    @NotNull
    private String tripDestination;

    @NotNull
    //@Future(message = "Trip date must be in the future")
    private LocalDateTime tripDate;

    private String tripDuration;

    private BigDecimal tripPrice;

    @Enumerated(EnumType.STRING)
    private TripType tripType;

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus = ReservationStatus.PENDING;

    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private BigDecimal latitude;

    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private BigDecimal longitude;

    @NotNull
    @Min(1)
    @Max(4)
    private Integer numberOfPassengers = 1;

    // Relations avec autres microservices : stocker uniquement l’ID
    private Integer driverId;
    private Integer userId;
    private Integer vehicleId;

    private Boolean readyForDriverRating = false;
    private Boolean readyForPassengerRating = false;
    private Boolean isRated = false;
    private Boolean reminderSent = false;
}