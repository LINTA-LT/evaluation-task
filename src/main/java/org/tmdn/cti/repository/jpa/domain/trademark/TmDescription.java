package org.tmdn.cti.repository.jpa.domain.trademark;



import lombok.Data;
import org.tmdn.cti.repository.jpa.domain.common.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MARKDESCRIPTION")
@Data
public class TmDescription extends BaseEntity {

	@Id
    @Column(name = "MARKDESCRIPTIONID")
    private Long id;

	@Column(name = "MARKID")
    private Long markId;

	@Column(name = "DESCRIPTION")
    private String description;

	@Column(name = "TYLANGCODE")
    private String languageCode;

}
