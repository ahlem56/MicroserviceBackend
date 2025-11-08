package tn.esprit.carpooling.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.carpooling.entities.Request;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> { }