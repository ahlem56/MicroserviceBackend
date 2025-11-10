package tn.esprit.parcel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.parcel.entities.Parcel;

import java.util.List;

public interface ParcelRepository extends JpaRepository<Parcel,Long> {
  // ✅ pour récupérer les colis d'un user
  List<Parcel> findByUserId(Long userId);
}
