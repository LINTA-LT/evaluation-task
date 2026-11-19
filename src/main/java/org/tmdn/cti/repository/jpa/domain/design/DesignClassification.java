package org.tmdn.cti.repository.jpa.domain.design;

import lombok.Data;
import org.tmdn.cti.repository.jpa.domain.common.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CLASSIFICATION")
@Data
public class DesignClassification extends BaseEntity {
    @Id
    @Column(name = "CLASSIFICATIONID")
    private Long id;

    @Column(name = "DESIGNID")
    private Long designId;

    @Column(name = "CLASSIFICATIONKINDCODE")
    private String classificationkindcode;

    @Column(name = "CLASSIFICATIONNUMBER")
    private String classNumber;

}
