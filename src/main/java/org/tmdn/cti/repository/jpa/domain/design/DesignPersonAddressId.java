package org.tmdn.cti.repository.jpa.domain.design;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class DesignPersonAddressId implements Serializable {

    @Column(name = "PERSONDESIGNID")
    private Long personDesignId;

    @Column(name = "ADDRESSID")
    private Long addressId;

}
