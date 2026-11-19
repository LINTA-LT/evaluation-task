package org.tmdn.cti.repository.jpa.domain.trademark;

import lombok.Data;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.tmdn.cti.repository.jpa.domain.common.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "OPPOSITION")
@Data
public class Opposition extends BaseEntity {

    @Id
    @Column(name = "OPPOSITIONID")
    private Long id;

    @Column(name = "MARKID")
    private Long markId;

    @Column(name = "DTOPPOSITION")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate oppositionDate;

    @Column(name = "IDENTIFIER")
    private String oppositionIdentifier;

    @Column(name = "STATUS")
    private String oppositionStatusCode;

    @Column(name = "DTSTATUS")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate oppositionStatusDate;

}
