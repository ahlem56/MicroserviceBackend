package tn.esprit.se3.reclamation.Controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import tn.esprit.se3.reclamation.DTO.ReclamationDTO;
import tn.esprit.se3.reclamation.Services.IReclamationService;

import java.util.List;

@RestController
@RequestMapping("/reclamations")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReclamationController {

    private final IReclamationService reclamationService;

    @PostMapping
    public ReclamationDTO createReclamation(@RequestBody ReclamationDTO dto) {
        return reclamationService.addReclamation(dto);
    }

    @GetMapping
    public List<ReclamationDTO> getAllReclamations() {
        return reclamationService.getAllReclamations();
    }

    @GetMapping("/{id}")
    public ReclamationDTO getReclamationById(@PathVariable Integer id) {
        return reclamationService.getReclamationById(id);
    }

    @PutMapping("/{id}")
    public ReclamationDTO updateReclamation(@PathVariable Integer id, @RequestBody ReclamationDTO dto) {
        return reclamationService.updateReclamation(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteReclamation(@PathVariable Integer id) {
        reclamationService.deleteReclamation(id);
    }

    @Value("${welcome.message}")
    private String welcomeMessage;

    @GetMapping("/welcome")
    public String welcome() {
        return welcomeMessage;
    }
}
