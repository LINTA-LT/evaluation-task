package org.tmdn.cti.repository.jpa.domain.trademark;

import lombok.Data;
import org.tmdn.cti.repository.jpa.domain.common.Publication;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "PUBLICATION")
@Data
public class TmPublication extends Publication {

    @Column(name = "MARKID")
    private Long markId;

    @Column(name = "PAGE")
    private String page;

}
