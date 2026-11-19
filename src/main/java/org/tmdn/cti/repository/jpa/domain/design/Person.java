package org.tmdn.cti.repository.jpa.domain.design;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.tmdn.cti.repository.jpa.domain.common.BasePerson;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Design specific Person defining relation to Design specific address
 */
@Entity
@Table(name = "PERSON")
@Data
@EqualsAndHashCode(callSuper = true)
public class Person extends BasePerson {

    @OneToMany
    @JoinColumn(name = "PERSONID")
    private List<Address> addresses;

}
