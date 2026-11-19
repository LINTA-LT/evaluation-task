package org.tmdn.cti.repository.jpa.domain.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BasePerson extends BaseEntity{

    @Id
    @Column(name = "PERSONID")
    private Long id;

    @Column(name = "IDENTIFIER")
    private String identifier;

    @Column(name = "TYNATIONALITYCODE")
    private String nationalityCode;

    @Column(name = "TYLEGALENTITY")
    private String legalEntity;

    @Embedded
    private Name name;

    @Transient
    public boolean isLegalEntity(){
        return "Legal entity".equals(getLegalEntity());
    }
}
