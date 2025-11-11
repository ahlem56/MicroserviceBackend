package org.event.demo.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehiculeDto {
    private Integer vehiculeId;
    private Integer driverId;
    private String model;
    private Integer capacity;
    private Integer serialNumber;
    private LocalDate maintenanceDate;
    private String insuranceStatus;
}

