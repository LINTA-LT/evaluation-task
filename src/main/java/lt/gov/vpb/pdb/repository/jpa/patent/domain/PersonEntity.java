package lt.gov.vpb.pdb.repository.jpa.patent.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "person")
@Getter
@Setter
public class PersonEntity {

    @Id
    @Column(name = "PERSON_ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "COUNTRY_CODE")
    private String countryCode;

    @Column(name = "CITY")
    private String city;

    @Column(name = "POSTAL_CODE")
    private String postalCode;

    @Column(name = "STREET_ADDRESS")
    private String address;
}
