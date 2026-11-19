package org.tmdn.cti.repository.jpa.domain.design;

import lombok.Getter;
import lombok.Setter;
import org.tmdn.cti.repository.jpa.domain.common.Record;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "DESIGNRECORD")
@Getter
@Setter
public class DesignRecord extends Record {

    @Id
    @Column(name = "DESIGNRECORDID")
    private Long id;

    @Column(name = "DESIGNID")
    private Long designId;

	@Column(name = "RECORDSUBTYPE")
	private String recordSubtype;

}
