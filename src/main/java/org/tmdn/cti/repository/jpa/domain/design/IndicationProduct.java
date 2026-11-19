package org.tmdn.cti.repository.jpa.domain.design;

import lombok.Data;
import org.tmdn.cti.repository.jpa.domain.common.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "INDICATIONPRODUCT")
@Data
public class IndicationProduct extends BaseEntity {
    @Id
    @Column(name = "INDICATIONPRODUCTID")
    private Long id;

    @Column(name = "DESIGNID")
    private Long designId;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "TYLANGCODE")
    private String languageCode;

    @Column(name = "SEQUENCENUMBER")
    private Integer sequenceNumber;

}
