package lt.gov.vpb.pdb.repository.jpa.patent.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Entity covers LT Patent and LT Patent Application data
 */
@Entity
@DiscriminatorValue("LtPatent")
@Getter
@Setter
public class LtPatentEntity extends DossierEntity {

    //f57
    @Column(name = "DESCRIPTION_LT")
    private String descriptionLt;

    //f57
    @Column(name = "DESCRIPTION_EN")
    private String descriptionEn;

    //f
    @Column(name = "TEXT_CONTENT_LT")
    @Basic(fetch=FetchType.LAZY)
    private String contentLt;

    //f85
    @Column(name = "PCT_NAT_LEVEL_START_DATE")
    private LocalDate pctNatLevelStartDate;

    //f
    @Column(name = "INVALIDATION_REASON")
    private String invalidationReason;

    @OneToMany(mappedBy = "dossier", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PctEntity> internationalData;

    @OneToMany(mappedBy = "dossier", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<DocumentEntity> documents;

    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.ALL)
    @Where(clause = "type in (2, 11)")
    private List<SPAEntity> spas;

}
