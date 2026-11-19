package lt.gov.vpb.pdb.repository.jpa.patent.domain;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table( name = "dossier_person_role")
@Data
public class DossierPersonRoleEntity  implements Serializable {

    @EmbeddedId
    private DossierPersonRoleId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("dossierId")
    @JoinColumn(name = "DOSSIER_ID")
    private DossierEntity dossier;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("personId")
    @JoinColumn(name = "PERSON_ID")
    private PersonEntity person;

    @Column(name = "PERSON_DOSSIER_ID")
    private Long personDossierId;

    @Column(name = "SANCTIONED")
    private Boolean sanctioned;
}
