package org.tmdn.cti.repository.jpa.domain.trademark;

import lombok.Data;
import org.tmdn.cti.repository.jpa.domain.common.Sound;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MARKSOUND")
@Data
public class TmSound extends Sound {

    @Id
    @Column(name = "MARKSOUNDID")
    private Long id;

    @Column(name = "MARKID")
    private Long markId;
    
}
