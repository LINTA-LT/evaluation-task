package org.tmdn.cti.repository.jpa.domain.design;

import lombok.Data;
import org.tmdn.cti.repository.jpa.domain.common.Publication;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "PUBLICATION")
@Data
public class DesignPublication extends Publication {

    @Column(name = "DESIGNID")
    private Long designid;

}
