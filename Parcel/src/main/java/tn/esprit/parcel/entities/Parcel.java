package tn.esprit.parcel.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parcel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long parcelId;

  @Enumerated(EnumType.STRING)
  private ParcelCategory parcelCategory;

  @NotNull
  private Integer recepeientPhoneNumber;

  @NotNull
  private Integer senderPhoneNumber;

  @NotNull
  private String parcelDeparture;

  @NotNull
  private String parcelDestination;

  private Integer parcelWeight;

  @Temporal(TemporalType.DATE)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC")
  private Date parcelDate = new Date();

  private float parcelPrice;

  @Enumerated(EnumType.STRING)
  private Status status = Status.PENDING;

  private boolean archived = false;

  // ✅ ID du user venant du MS user (Symfony)
  private Long userId;

  // Damaged parcel
  private String damageImageUrl;
  private String damageDescription;
  private LocalDateTime damageReportedAt;
}
