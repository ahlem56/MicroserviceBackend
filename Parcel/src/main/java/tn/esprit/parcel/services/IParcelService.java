package tn.esprit.parcel.services;

import tn.esprit.parcel.entities.Parcel;

import java.util.List;

public interface IParcelService {
  Parcel addParcel(Parcel parcel, Long userId);  // ✅ maj
  Parcel updateParcel(Long id, Parcel parcel);
  void deleteParcel(Long id);
  Parcel getParcelById(Long id);
  List<Parcel> getAllParcels();

  float calculatePriceByWeight(int weight);

  // ✅ extra : récupérer les colis d’un user
  List<Parcel> getParcelsByUserId(Long userId);
}
