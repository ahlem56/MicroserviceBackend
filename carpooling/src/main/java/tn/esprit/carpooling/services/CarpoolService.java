package tn.esprit.carpooling.services;

import tn.esprit.carpooling.entities.Carpool;
import tn.esprit.carpooling.repositories.CarpoolRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarpoolService {

    private final CarpoolRepository carpoolRepository;

    public CarpoolService(CarpoolRepository carpoolRepository) {
        this.carpoolRepository = carpoolRepository;
    }

    public List<Carpool> getAllCarpools() {
        return carpoolRepository.findAll();
    }

    public Carpool addCarpool(Carpool carpool) {
        return carpoolRepository.save(carpool);
    }

    public List<Carpool> findByDestination(String destination) {
        return carpoolRepository.findByDestination(destination);
    }

    public void deleteCarpool(Long id) {
        carpoolRepository.deleteById(id);
    }
}
