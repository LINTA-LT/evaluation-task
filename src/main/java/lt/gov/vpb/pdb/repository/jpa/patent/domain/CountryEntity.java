package lt.gov.vpb.pdb.repository.jpa.patent.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "country")
@Getter
@Setter
public class CountryEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "ISO2")
    private String iso2;

    @Column(name = "ISO3")
    private String iso3;

    @Column(name = "NAME_LT")
    private String nameLt;

    @Column(name = "NAME_EN")
    private String nameEn;

    @Column(name = "ACTIVE")
    private Boolean active;

}
