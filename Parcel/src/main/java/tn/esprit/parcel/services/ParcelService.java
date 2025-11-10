package tn.esprit.parcel.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.parcel.entities.Parcel;
import tn.esprit.parcel.entities.Status;
import tn.esprit.parcel.repositories.ParcelRepository;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class ParcelService implements IParcelService {

  private final ParcelRepository parcelRepository;

  @Override
  public Parcel addParcel(Parcel parcel, Long userId) {
    parcel.setUserId(userId);                              // ✅ lien user
    parcel.setParcelDate(new Date());
    parcel.setParcelPrice(calculatePriceByWeight(parcel.getParcelWeight()));
    parcel.setStatus(Status.PENDING);
    return parcelRepository.save(parcel);
  }

  @Override
  public Parcel updateParcel(Long id, Parcel updatedParcel) {
    return parcelRepository.findById(id)
      .map(existing -> {
        existing.setParcelCategory(updatedParcel.getParcelCategory());
        existing.setRecepeientPhoneNumber(updatedParcel.getRecepeientPhoneNumber());
        existing.setSenderPhoneNumber(updatedParcel.getSenderPhoneNumber());
        existing.setParcelDeparture(updatedParcel.getParcelDeparture());
        existing.setParcelDestination(updatedParcel.getParcelDestination());
        existing.setParcelWeight(updatedParcel.getParcelWeight());
        existing.setParcelDate(updatedParcel.getParcelDate());
        existing.setParcelPrice(calculatePriceByWeight(updatedParcel.getParcelWeight()));
        existing.setStatus(updatedParcel.getStatus());
        existing.setArchived(updatedParcel.isArchived());
        existing.setDamageImageUrl(updatedParcel.getDamageImageUrl());
        existing.setDamageDescription(updatedParcel.getDamageDescription());
        existing.setDamageReportedAt(updatedParcel.getDamageReportedAt());
        // on ne change pas userId ici (sauf besoin spécifique)
        return parcelRepository.save(existing);
      })
      .orElseThrow(() -> new RuntimeException("Parcel not found with id: " + id));
  }

  @Override
  public void deleteParcel(Long id) {
    parcelRepository.deleteById(id);
  }

  @Override
  public Parcel getParcelById(Long id) {
    return parcelRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Parcel not found with id: " + id));
  }

  @Override
  public List<Parcel> getAllParcels() {
    return parcelRepository.findAll();
  }

  @Override
  public float calculatePriceByWeight(int weight) {
    if (weight <= 0) return 0;
    else if (weight <= 10) return 5f;
    else if (weight <= 20) return 10f;
    else if (weight <= 30) return 15f;
    else return 20f;
  }

  @Override
  public List<Parcel> getParcelsByUserId(Long userId) {
    return parcelRepository.findByUserId(userId);
  }
}
