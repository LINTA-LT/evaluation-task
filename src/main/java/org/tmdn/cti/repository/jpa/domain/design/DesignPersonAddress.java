package org.tmdn.cti.repository.jpa.domain.design;

import lombok.Data;
import org.tmdn.cti.repository.jpa.domain.common.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "PERSONDESIGNADDRESS")
@Data
public class DesignPersonAddress extends BaseEntity {

    @Id
    @Column(name = "PERSONDESIGNID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PERSONDESIGNID", insertable = false, updatable = false)
    private DesignPerson designPerson;

    @Column(name = "ISPREMISES")
    private Boolean premises;

    @Column(name = "ISPRIMARY")
    private Boolean primary;

    @ManyToOne
    @JoinColumn(name = "ADDRESSID", insertable = false, updatable = false)
    private Address address;

}
