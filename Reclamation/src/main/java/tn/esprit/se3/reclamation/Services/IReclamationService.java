package tn.esprit.se3.reclamation.Services;

import tn.esprit.se3.reclamation.DTO.ReclamationDTO;
import java.util.List;

public interface IReclamationService {
    ReclamationDTO addReclamation(ReclamationDTO dto);
    List<ReclamationDTO> getAllReclamations();
    ReclamationDTO getReclamationById(Integer id);
    ReclamationDTO updateReclamation(Integer id, ReclamationDTO dto);
    void deleteReclamation(Integer id);
}
