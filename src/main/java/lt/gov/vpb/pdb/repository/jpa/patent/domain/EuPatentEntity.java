package lt.gov.vpb.pdb.repository.jpa.patent.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("EuPatent")
@Getter
@Setter
public class EuPatentEntity extends DossierEntity {

    @OneToOne
    @JoinColumn(name = "EU_DOSSIER_ID", referencedColumnName = "EU_DOSSIER_ID")
    private EuDossierEntity euDossier;

    @OneToMany(mappedBy = "dossier", orphanRemoval = true, cascade = CascadeType.ALL)
    @Where(clause = "type = 'EURO_PCT'")
    private List<PctEntity> pctData;

    @OneToMany(mappedBy = "dossier", orphanRemoval = true, cascade = CascadeType.ALL)
    @Where(clause = "type != 'EURO_PCT'")
    private List<PctEntity> epData;

    @OneToMany(mappedBy = "dossier", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<DocumentEntity> documents;

    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.ALL)
    @Where(clause = "type in (23, 24)")
    private List<SPAEntity> spas;

}
