package org.event.demo.Controller;

import org.event.demo.Dto.VehiculeDto;
import org.event.demo.Service.VehiculeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicules")
public class VehiculeController {
    private VehiculeService service;

    public VehiculeController(VehiculeService service) {
        this.service = service;
    }

    @PostMapping
    public VehiculeDto create(@RequestBody VehiculeDto dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<VehiculeDto> listAll() {
        return service.listAll();
    }

    @PutMapping("/{id}")
    public VehiculeDto update(@PathVariable Integer id, @RequestBody VehiculeDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    @Value("${welcome.message}")
    private String welcomeMessage; // ✅ Ceci fonctionne maintenant

    @GetMapping("/welcome")
    public String welcome() {
        return welcomeMessage;
    }

}
