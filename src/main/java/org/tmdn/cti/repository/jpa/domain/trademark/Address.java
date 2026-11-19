package org.tmdn.cti.repository.jpa.domain.trademark;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.tmdn.cti.repository.jpa.domain.common.BaseAddress;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ADDRESS")
@Data
@EqualsAndHashCode(callSuper = true)
public class Address extends BaseAddress {

    @ManyToOne
    @JoinColumn(name = "PERSONID", insertable = false, updatable = false)
    private Person person;

    @OneToMany(mappedBy = "address")
    private List<MarkPersonAddress> markPersonAddresses;

}
