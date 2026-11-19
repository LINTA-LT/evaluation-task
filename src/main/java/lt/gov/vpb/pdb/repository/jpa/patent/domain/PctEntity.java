package lt.gov.vpb.pdb.repository.jpa.patent.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pct")
@Getter
@Setter
public class PctEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "DOSSIER_ID")
    private DossierEntity dossier;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "COUNTRY_CODE")
    private String countryCode;

    @Column(name = "INTL_APPLICATION_NUMBER")
    private String applicationNumber;

    @Column(name = "INTL_APPLICATION_DATE")
    private LocalDate applicationDate;

    @Column(name = "INTL_APPLICATION_PUB_NUMBER")
    private String applicationPubNumber;

    @Column(name = "INTL_APPLICATION_PUB_DATE")
    private LocalDate applicationPubDate;

    @Column(name = "INTL_PATENT_ISSUE_DATE")
    private LocalDate intlPatentIssueDate;

    @Column(name = "INTL_PATENT_ISSUE_PUB_DATE")
    private LocalDate intlPatentIssuePubDate;


}
