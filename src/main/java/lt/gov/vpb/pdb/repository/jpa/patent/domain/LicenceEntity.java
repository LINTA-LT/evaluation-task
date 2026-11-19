package lt.gov.vpb.pdb.repository.jpa.patent.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@DiscriminatorValue("Licence")
@Getter
@Setter
public class LicenceEntity extends DossierEntity{

    @ManyToOne
    @JoinColumn(name = "PARENT_DOSSIER_ID")
    private DossierEntity parent;

    @Column(name = "LICENCE_TYPE")
    private Long licenceType;

    @Column(name = "LICENCE_EXPIRY_DATE")
    private LocalDate licenceExpiryDate;

    @Column(name = "AGREEMENT_RECORD_DATE")
    private LocalDate agreementRecordDate;

    @OneToMany(mappedBy = "dossier")
    @Where(clause = "role_id in ('6')")
    @OrderBy("personDossierId")
    private List<DossierPersonRoleEntity> licenciates;

}
