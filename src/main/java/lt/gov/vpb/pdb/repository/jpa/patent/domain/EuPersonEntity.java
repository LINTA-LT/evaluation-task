package lt.gov.vpb.pdb.repository.jpa.patent.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "eu_person")
@Getter
@Setter
public class EuPersonEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "EU_DOSSIER_ID")
    private EuDossierEntity dossier;

    @Column(name = "ROLE_ID")
    private Long roleId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "COUNTRY_CODE")
    private String countryCode;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "CITY")
    private String city;
}
