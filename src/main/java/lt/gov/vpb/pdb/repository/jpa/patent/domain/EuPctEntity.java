package lt.gov.vpb.pdb.repository.jpa.patent.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "eu_pct")
@Getter
@Setter
public class EuPctEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "EU_DOSSIER_ID")
    private EuDossierEntity dossier;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "INTL_APPLICATION_NUMBER")
    private String applicationNumber;

    @Column(name = "INTL_APPLICATION_DATE")
    private LocalDate applicationDate;

    @Column(name = "INTL_APPLICATION_PUB_NUMBER")
    private String applicationPubNumber;

    @Column(name = "INTL_APPLICATION_PUB_DATE")
    private LocalDate applicationPubDate;

}
