package org.tmdn.cti.repository.jpa.domain.design;

import lombok.Data;
import org.tmdn.cti.repository.jpa.domain.common.Image;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "DESIGNIMAGE")
@Data
public class DesignImage extends Image {

    @Id
    @Column(name = "DESIGNIMAGEID")
    private Long id;

    @Column(name = "DESIGNID")
    private Long designId;

    @Column(name = "SEQUENCENUMBER")
    private int sequenceNumber;

}
