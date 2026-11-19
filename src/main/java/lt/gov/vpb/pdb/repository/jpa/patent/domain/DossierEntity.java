package lt.gov.vpb.pdb.repository.jpa.patent.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DiscriminatorFormula;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "dossier")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorFormula("case " +
        "when TYPE in (0, 1) then 'LtPatent' " +
        "when TYPE in (2, 11, 23, 24) then 'SPA' " +
        "when TYPE in (20, 21, 22) then 'EuPatent' " +
        "when TYPE in (13, 25) then 'Licence' " +
        "else 'Unknown' end")
@Getter
@Setter
public class DossierEntity {

    @Id
    @Column(name = "DOSSIER_ID")
    private Long id;

    /**
     * for Patents: the number contains the patent number
     * for others: number contains a parent-dossier patent-number. In this case the parent_dossier_id always contains
     * a value of parent dossier id.
     */
    @Column(name = "NUMBER")
    private String number;

    @Column(name = "TYPE")
    private Long type;

    //f21
    @Column(name = "APPLICATION_NUMBER")
    private String applicationNumber;

    //f22
    @Column(name = "APPLICATION_DATE")
    private LocalDate applicationDate;

    @Column(name = "APPLICATION_PUB_DATE")
    private LocalDate applicationPubDate;

    @Column(name = "PATENT_PUB_DATE")
    private LocalDate patentPubDate;

    @Column(name = "TITLE_LT")
    private String titleLt;

    @Column(name = "TITLE_EN")
    private String titleEn;

    @Column(name = "DESCRIPTION_LT")
    private String descriptionLt;

    @Column(name = "DESCRIPTION_EN")
    private String descriptionEn;

    @ManyToOne
    @JoinColumn(name = "STATUS_CODE_ID")
    private StatusEntity status;

    @OneToMany(mappedBy = "dossier", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ClassificationEntity> classifications;

    @OneToMany(mappedBy = "dossier", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PriorityEntity> priorities;

    @Column(name = "LAST_FEE_DATE")
    private LocalDate lastFeeDate;

    @Column(name = "LAST_FEE_VALIDITY_YEARS")
    private Long lastFeeValidityYears;

    @Column(name = "LAST_FEE_AMOUNT")
    private Long lastFeeAmountCents;

    @Column(name = "LAST_FEE_CURRENCY")
    private String lastFeeCurrency;

    @Column(name = "NEXT_FEE_DUE_DATE")
    private LocalDate nextFeeDate;

    @Column(name = "INVALIDATION_DATE")
    private LocalDate invalidationDate;

    @Column(name = "BO_LAST_UPDATE_DATE")
    private LocalDate boLastUpdateDate;

    //f71
    @OneToMany(mappedBy = "dossier")
    @Where(clause = "role_id = 1")
    @OrderBy("personDossierId")
    private List<DossierPersonRoleEntity> applicants;

    @OneToMany(mappedBy = "dossier")
    @Where(clause = "role_id = 2")
    @OrderBy("personDossierId")
    private List<DossierPersonRoleEntity> inventors;

    @OneToMany(mappedBy = "dossier")
    @Where(clause = "role_id = 3")
    @OrderBy("personDossierId")
    private List<DossierPersonRoleEntity> owners;

    @OneToMany(mappedBy = "dossier")
    @Where(clause = "role_id in (4, 5)")
    @OrderBy("personDossierId")
    private List<DossierPersonRoleEntity> representatives;

    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.ALL)
    @Where(clause = "type in (13, 25)")
    private List<LicenceEntity> licences;

}
