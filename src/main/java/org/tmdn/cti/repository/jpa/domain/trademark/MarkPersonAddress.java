package org.tmdn.cti.repository.jpa.domain.trademark;

import lombok.Data;
import org.tmdn.cti.repository.jpa.domain.common.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "PERSONMARKADDRESS")
@Data
public class MarkPersonAddress extends BaseEntity {

    @EmbeddedId
    private MarkPersonAddressId id;

    @ManyToOne
    @JoinColumn(name = "PERSONMARKID", insertable = false, updatable = false)
    private MarkPerson markPerson;

    @ManyToOne
    @JoinColumn(name = "ADDRESSID", insertable = false, updatable = false)
    private Address address;

    @Column(name = "ISPREMISES")
    private Boolean premises;

    @Column(name = "ISPRIMARY")
    private Boolean primary;


}
