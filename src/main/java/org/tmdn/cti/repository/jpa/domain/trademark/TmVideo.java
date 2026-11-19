package org.tmdn.cti.repository.jpa.domain.trademark;

import lombok.Data;
import org.tmdn.cti.repository.jpa.domain.common.Sound;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MARKVIDEO")
@Data
public class TmVideo extends Sound {

    @Id
    @Column(name = "MARKVIDEOID")
    private Long id;

    @Column(name = "MARKID")
    private Long markId;
    
}
