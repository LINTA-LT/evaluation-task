package org.tmdn.cti.repository.jpa.domain.common;

import lombok.Data;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
public class ExhibitionPriority extends BaseEntity {

    @Id
    @Column(name = "EXHIBITIONPRIORITYID")
    private Long id;

    @Column(name = "NAME")
    private String exhibitionName;

    @Column(name = "DTEXHIBITION")

    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate exhibitionDate;

    @Column(name = "TYSTATUSCODE")
    private String exhibitionStatusCode;

}
