package lt.gov.vpb.pdb.repository.jpa.patent.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "priority")
@Getter
@Setter
public class PriorityEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "DOSSIER_ID")
    private DossierEntity dossier;

    @Column(name = "PRIORITY_NUMBER")
    private String number;

    @Column(name = "PRIORITY_DATE")
    private LocalDate date;

    @Column(name = "PRIORITY_COUNTRY_CODE")
    private String countryCode;

}
