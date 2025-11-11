package tn.esprit.se3.reclamation.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.se3.reclamation.entities.Reclamation;

@Repository
public interface ReclamationRepository extends JpaRepository<Reclamation,Integer>{
}
