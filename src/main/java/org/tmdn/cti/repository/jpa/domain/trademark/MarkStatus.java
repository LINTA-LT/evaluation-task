package org.tmdn.cti.repository.jpa.domain.trademark;

import lombok.Data;

import javax.persistence.*;


@Entity
@Table(name = "MARKSTATUSCODE")
@Data
public class MarkStatus {
	
	@Id
    @GeneratedValue
    @Column(name = "MARKSTATUSCODEID")
    private Long id;
	
	@Column(name = "DESCRIPTION")
    private String description;
	
	@Column(name = "DESCRIPTIONCI")
    private String descriptionCI;

}
