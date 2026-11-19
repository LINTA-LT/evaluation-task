package lt.gov.vpb.pdb.repository.jpa.patent.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class DossierPersonRoleId implements Serializable {

    @Column(name = "DOSSIER_ID")
    private Long dossierId;

    @Column(name = "PERSON_ID")
    private Long personId;

    @Column(name = "ROLE_ID")
    private Long roleId;
}
