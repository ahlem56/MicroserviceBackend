package tn.esprit.se3.reclamation.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.se3.reclamation.DTO.ReclamationDTO;
import tn.esprit.se3.reclamation.entities.Reclamation;
import tn.esprit.se3.reclamation.Repositories.ReclamationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReclamationServices implements IReclamationService {

    private final ReclamationRepository reclamationRepository;

    @Override
    public ReclamationDTO addReclamation(ReclamationDTO DTO) {
        Reclamation reclamation = new Reclamation();
        reclamation.setUserId(DTO.getUserId());
        reclamation.setIssueDescription(DTO.getIssueDescription());
        reclamation.setCreatedDate(DTO.getCreatedDate());
        Reclamation saved = reclamationRepository.save(reclamation);
        return mapToDTO(saved);
    }

    @Override
    public List<ReclamationDTO> getAllReclamations() {
        return reclamationRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReclamationDTO getReclamationById(Integer id) {
        Reclamation rec = reclamationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reclamation not found with ID: " + id));
        return mapToDTO(rec);
    }

    @Override
    public ReclamationDTO updateReclamation(Integer id, ReclamationDTO dto) {
        Reclamation rec = reclamationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reclamation not found with ID: " + id));

        rec.setUserId(dto.getUserId());
        rec.setIssueDescription(dto.getIssueDescription());
        rec.setCreatedDate(dto.getCreatedDate());

        return mapToDTO(reclamationRepository.save(rec));
    }

    @Override
    public void deleteReclamation(Integer id) {
        reclamationRepository.deleteById(id);
    }

    private ReclamationDTO mapToDTO(Reclamation r) {
        return new ReclamationDTO(
                r.getReportId(),
                r.getUserId(),
                r.getIssueDescription(),
                r.getCreatedDate()
        );
    }
}
