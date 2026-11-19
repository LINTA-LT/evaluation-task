package org.tmdn.cti.repository.jpa.domain.design;

import lombok.Data;
import org.tmdn.cti.repository.jpa.domain.common.Priority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "PRIORITY")
@Data
public class DesignPriority extends Priority{
    
    @Column(name = "DESIGNID")
    private Long designId;
    
}
