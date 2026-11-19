package lt.gov.vpb.pdb.repository.jpa.patent.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("SPA")
@Getter
@Setter
public class SPAEntity extends DossierEntity{

    @ManyToOne
    @JoinColumn(name = "PARENT_DOSSIER_ID")
    private DossierEntity parent;

    @Column(name = "F92_NUMBER")
    private String f92Number;

    @Column(name = "F92_DATE")
    private LocalDate f92Date;

    @Column(name = "F93_NUMBER")
    private String f93Number;

    @Column(name = "F93_DATE")
    private LocalDate f93Date;

    @Column(name = "F93_PUB_DATE")
    private LocalDate f93PubDate;

    @Column(name = "F41")
    private LocalDate spcApplicationPubDate;

    @Column(name = "F24")
    private LocalDate spcEffectiveDate;

    @Column(name = "F94")
    private LocalDate spcExpiryDate;

    @Column(name = "F95")
    private String protectedProductName;

    @Column(name = "SPA_EXTENSION_APPLICATION_DATE")
    private LocalDate extensionApplicationDate;

    @Column(name = "SPA_EXTENSION_EXPIRY_DATE")
    private LocalDate extensionExpiryDate;

    @Column(name = "SPC_PUBLICATION_DATE")
    private LocalDate spcPublicationDate;

}
