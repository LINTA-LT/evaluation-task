package org.tmdn.cti.repository.jpa.domain.design;

import lombok.Data;
import org.tmdn.cti.repository.jpa.domain.common.BaseEntity;
import org.tmdn.cti.repository.jpa.domain.common.Role;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "DESIGNPERSON")
@Data
public class DesignPerson extends BaseEntity {

    @Id
    @Column(name = "PERSONDESIGNID")
    private int id;

    @Column(name = "DESIGNID")
    private Long designId;

    @Enumerated
    @Column(name = "ROLEIDENTIFIER")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "PERSONID")
    private Person person;

    @OneToMany(mappedBy = "designPerson")
    private List<DesignPersonAddress> designPersonAddresses;

    public boolean isApplicant() {
        return role == Role.APPLICANT;
    }

    public boolean isRepresentative() {
        return getRole() == Role.REPRESENTATIVE || getRole() == Role.REPRESENTATIVE_SPECIAL;
    }

    public boolean isDesigner() {
        return role == Role.DESIGNER;
    }
    
    public boolean isPreviousApplicant() {
        return role == Role.PREVIOUS_APPLICANT;
    }
}
