package tn.esprit.carpooling.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.carpooling.entities.Carpool;

import java.util.List;

@Repository
public interface CarpoolRepository extends JpaRepository<Carpool, Long> {
    List<Carpool> findByDestination(String destination);
}