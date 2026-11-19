package org.tmdn.cti.repository.jpa.domain.trademark;

import lombok.Data;
import org.tmdn.cti.repository.jpa.domain.common.BaseEntity;
import org.tmdn.cti.repository.jpa.domain.common.Role;
import org.tmdn.cti.repository.jpa.domain.common.RoleEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "PERSONMARK")
@Data
public class MarkPerson extends BaseEntity {

    @Id
    @Column(name = "PERSONMARKID")
    private Long id;

    @Column(name = "MARKID")
    private Long markId;

    @Column(name = "PERSONID")
    private Long personId;

    @Column(name = "ROLEIDENTIFIER")
    private Short roleId;
    
    @ManyToOne
    @JoinColumn(name = "ROLEIDENTIFIER", insertable = false, updatable = false)
    private RoleEntity roleEntity;
    
    @ManyToOne
    @JoinColumn(name = "PERSONID", insertable = false, updatable = false)
    private Person person;

    @OneToMany(mappedBy = "markPerson")
    private List<MarkPersonAddress> markPersonAddresses;

    public Role getRole() {
		return Role.fromDescription(roleEntity.getDescription());
	}

	public boolean isApplicant() {
        return getRole() == Role.APPLICANT;
    }
	
	public boolean isRepresentative() {
        return getRole() == Role.REPRESENTATIVE || getRole() == Role.REPRESENTATIVE_SPECIAL;
    }
}
