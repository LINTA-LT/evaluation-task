package org.tmdn.cti.repository.jpa.domain.trademark;

import lombok.Data;
import org.tmdn.cti.repository.jpa.domain.common.Priority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "PRIORITY")
@Data
public class TmPriority extends Priority{
    
    @Column(name = "MARKID")
    private Long markId;

    @Column(name = "TYSOVIETUNION")
    private String sovietUnionType;

}
