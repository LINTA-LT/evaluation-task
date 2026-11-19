package org.tmdn.cti.repository.jpa.domain.design;

import lombok.Data;

import javax.persistence.*;


@Entity
@Table(name = "DESIGNSTATUSCODE")
@Data
public class DesignStatus {
	
	@Id
    @GeneratedValue
    @Column(name = "DESIGNSTATUSCODEID")
    private Long id;

	@Column(name = "DESCRIPTION")
    private String description;

}
