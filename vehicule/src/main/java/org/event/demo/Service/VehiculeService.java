package org.event.demo.Service;

import org.event.demo.Dto.VehiculeDto;
import org.event.demo.Model.Vehicule;
import org.event.demo.Repository.VehiculeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class VehiculeService implements IVehiculeService{
    private VehiculeRepository repo;
    public VehiculeService(VehiculeRepository repo) {
        this.repo = repo;
    }

    @Override
    public VehiculeDto create(VehiculeDto dto) {
        Vehicule vehicule = new Vehicule();
        vehicule.setDriverId(dto.getDriverId());
        vehicule.setModel(dto.getModel());
        vehicule.setCapacity(dto.getCapacity());
        vehicule.setSerialNumber(dto.getSerialNumber());
        vehicule.setMaintenanceDate(dto.getMaintenanceDate());
        vehicule.setInsuranceStatus(dto.getInsuranceStatus());

        Vehicule saved = repo.save(vehicule);

        dto.setVehiculeId(saved.getVehiculeId());
        return dto;    }
    @Override
    public List<VehiculeDto> listAll() {
        return repo.findAll().stream()
                .map(v -> new VehiculeDto(
                        v.getVehiculeId(),
                        v.getDriverId(),
                        v.getModel(),
                        v.getCapacity(),
                        v.getSerialNumber(),
                        v.getMaintenanceDate(),
                        v.getInsuranceStatus()
                ))
                .toList();
    }

    @Override
    public VehiculeDto update(Integer id, VehiculeDto dto) {
        Vehicule vehicule = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicule not found with id: " + id));

        vehicule.setDriverId(dto.getDriverId());
        vehicule.setModel(dto.getModel());
        vehicule.setCapacity(dto.getCapacity());
        vehicule.setSerialNumber(dto.getSerialNumber());
        vehicule.setMaintenanceDate(dto.getMaintenanceDate());
        vehicule.setInsuranceStatus(dto.getInsuranceStatus());

        Vehicule updated = repo.save(vehicule);

        return new VehiculeDto(
                updated.getVehiculeId(),
                updated.getDriverId(),
                updated.getModel(),
                updated.getCapacity(),
                updated.getSerialNumber(),
                updated.getMaintenanceDate(),
                updated.getInsuranceStatus()
        );
    }

    @Override
    public void delete(Integer id) {
        repo.deleteById(id);
    }

    }

