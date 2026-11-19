package lt.gov.vpb.pdb.model;

import lombok.*;
import lt.gov.vpb.pdb.repository.jpa.patent.domain.DossierPersonRoleEntity;
import lt.gov.vpb.pdb.repository.jpa.patent.domain.EuPersonEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    private String freeformName;
    private String freeformAddress;
    private String country;
    private boolean sanctionsApplied;

    public static Person from(DossierPersonRoleEntity entity) {
        if (entity == null) {
            return null;
        }
        return Person.builder()
                .freeformName(entity.getPerson().getName())
                .freeformAddress("")
                .country(entity.getPerson().getCountryCode())
                .build();
    }

    public static Person from(EuPersonEntity entity) {
        if (entity == null) {
            return null;
        }
        return Person.builder()
                .freeformName(entity.getName())
                .freeformAddress("")
                .country(entity.getCountryCode())
                .build();
    }

}
