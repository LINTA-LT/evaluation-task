package org.tmdn.cti.repository.jpa.domain.common;

import lombok.Data;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

@MappedSuperclass
@Data
public class Record extends BaseEntity {
    
    @Column(name = "IDENTIFIER")
    private String identifier;

    @Column(name = "BASICRECORDKIND")
    private String basicRecordKind;

    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @Column(name = "DTPUBLICATION")
    private LocalDate publicationDate;

    @Column(name = "TYSTATUSCODE")
    private String statusCode;

    @Column(name = "DTFILING")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate filingDate;

    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @Column(name = "DTNOTIFICATION")
    private LocalDate notificationDate;

    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @Column(name = "DTEFFECTIVE")
    private LocalDate effectiveDate;

    @Column(name = "RECORDREFERENCE")
    private String recordReference;

    @Column(name = "DTSTATUS")
    private Timestamp statusDate;

}
