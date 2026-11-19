package org.tmdn.cti.repository.jpa.domain.common;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "ROLE")
@Data
public class RoleEntity {
	
    @Id
    @Column(name = "ROLEID")
    private Integer id;
    
    @Column(name = "DESCRIPTION")
    private String description;

}
