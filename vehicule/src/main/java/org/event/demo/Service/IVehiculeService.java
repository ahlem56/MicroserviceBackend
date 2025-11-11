package org.event.demo.Service;

import org.event.demo.Dto.VehiculeDto;

import java.util.List;

public interface IVehiculeService {
    VehiculeDto create(VehiculeDto dto);
    List<VehiculeDto> listAll();
    VehiculeDto update(Integer id, VehiculeDto dto);
    void delete(Integer id);
}
