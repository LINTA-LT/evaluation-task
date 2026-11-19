package lt.gov.vpb.pdb.repository.jpa.patent.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "eu_dossier")
@Getter
@Setter
public class EuDossierEntity {

    @Id
    @Column(name = "EU_DOSSIER_ID")
    private Long id;

    @OneToOne(mappedBy = "euDossier")
    private EuPatentEntity euPatent;


    @Column(name = "PATENT_NUMBER")
    private String patentNumber;

    //f21
    @Column(name = "APPLICATION_NUMBER")
    private String applicationNumber;

    //f22
    @Column(name = "APPLICATION_DATE")
    private LocalDate applicationDate;

    @Column(name = "APPLICATION_PUB_DATE")
    private LocalDate applicationPubDate;

    //F45
    @Column(name = "PATENT_PUB_DATE")
    private LocalDate patentPubDate;

    //F46
    @Column(name = "F46_DATE")
    private LocalDate f46Date;

    @Column(name = "TITLE_LT")
    private String titleLt;

    @Column(name = "TITLE_EN")
    private String titleEn;

    @Column(name = "STATUS_CODE")
    private String statusCode;

    @Column(name = "CAME_INTO_FORCE_IN_LT")
    private Boolean effectiveInLt;

    @OneToMany(mappedBy = "dossier", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<EuClassificationEntity> classifications;

    @OneToMany(mappedBy = "dossier", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<EuPriorityEntity> priorities;

    @OneToMany(mappedBy = "dossier", orphanRemoval = true, cascade = CascadeType.ALL)
    @Where(clause = "type = 'EURO_PCT'")
    private List<EuPctEntity> pctData;

    @OneToMany(mappedBy = "dossier", orphanRemoval = true, cascade = CascadeType.ALL)
    @Where(clause = "type != 'EURO_PCT'")
    private List<EuPctEntity> epData;

    @Column(name = "INVALIDATION_DATE")
    private LocalDate invalidationDate;

    @Column(name = "BO_LAST_UPDATE_DATE")
    private LocalDate boLastUpdateDate;

    //f71
    @OneToMany(mappedBy = "dossier", orphanRemoval = true, cascade = CascadeType.ALL)
    @Where(clause = "role_id = 1")
    private List<EuPersonEntity> applicants;

    @OneToMany(mappedBy = "dossier", orphanRemoval = true, cascade = CascadeType.ALL)
    @Where(clause = "role_id = 2")
    private List<EuPersonEntity> inventors;

    @OneToMany(mappedBy = "dossier", orphanRemoval = true, cascade = CascadeType.ALL)
    @Where(clause = "role_id = 3")
    private List<EuPersonEntity> owners;

    @OneToMany(mappedBy = "dossier", orphanRemoval = true, cascade = CascadeType.ALL)
    @Where(clause = "role_id in (4, 5)")
    private List<EuPersonEntity> representatives;

}
