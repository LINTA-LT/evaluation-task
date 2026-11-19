package org.tmdn.cti.repository.jpa.domain.common;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
public class Sound extends BaseEntity {

    @Column(name = "FILENAME")
    private String filename;

}
