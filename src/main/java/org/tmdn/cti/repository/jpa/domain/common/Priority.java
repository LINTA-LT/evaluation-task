package org.tmdn.cti.repository.jpa.domain.common;

import lombok.Data;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;


@MappedSuperclass
@Data
public abstract class Priority extends BaseEntity {

    @Id
    @Column(name = "PRIORITYID")
    private Long id;

    @Column(name = "TYCOUNTRYCODE")
    private String priorityCountryCode;

    @Column(name = "DTPRIORITY")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate priorityDate;

    @Column(name = "PRIORITYNUMBER")
    private String priorityNumber;

}
