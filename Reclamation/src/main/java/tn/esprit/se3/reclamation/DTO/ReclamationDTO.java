package tn.esprit.se3.reclamation.DTO;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReclamationDTO {
    Integer reportId;
    Integer userId;
    String issueDescription;
    LocalDate createdDate;
}
