package org.tmdn.cti.repository.jpa.domain.common;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
@Data
public abstract class BaseEntity {

    @Column(name = "DTINSERT")
    private Date insertedAt;

    @Column(name = "ACTIVEROW")
    private boolean active;

    @Column(name = "DUPLICATEROW")
    private boolean duplicate;

}
