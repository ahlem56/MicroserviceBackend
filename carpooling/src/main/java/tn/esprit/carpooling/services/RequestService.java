package tn.esprit.carpooling.services;

import tn.esprit.carpooling.entities.Carpool;
import tn.esprit.carpooling.entities.Request;
import tn.esprit.carpooling.repositories.CarpoolRepository;
import tn.esprit.carpooling.repositories.RequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final CarpoolRepository carpoolRepository;

    public RequestService(RequestRepository requestRepository, CarpoolRepository carpoolRepository) {
        this.requestRepository = requestRepository;
        this.carpoolRepository = carpoolRepository;
    }

    @Transactional
    public Request addRequest(Request request) {
        Carpool carpool = request.getCarpool();
        if (carpool.getAvailableSeats() <= 0) {
            throw new IllegalStateException("No seats available for this ride.");
        }
        carpool.setAvailableSeats(carpool.getAvailableSeats() - 1);
        carpoolRepository.save(carpool);
        return requestRepository.save(request);
    }

    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }
}
