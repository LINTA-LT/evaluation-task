package org.tmdn.cti.repository.jpa.domain.trademark;

import lombok.Data;
import org.tmdn.cti.repository.jpa.domain.common.Record;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MARKRECORD")
@Data
public class MarkRecord extends Record {
    @Id
    @Column(name = "MARKRECORDID")
    private Long id;

    @Column(name = "MARKID")
    private Long markid;

    @Column(name = "RECORDSUBTYPE")
    private String recordSubType;

}
