package org.tmdn.cti.repository.jpa.domain.trademark;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class MarkPersonAddressId implements Serializable {

    @Column(name = "PERSONMARKID")
    private Long markPersonId;

    @Column(name = "ADDRESSID")
    private Long addressId;

}
