package org.tmdn.cti.repository.jpa.domain.common;

import lombok.Data;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
@Data
public class IPRight extends BaseEntity {

    @Column(name = "TYREGOFFICECODE")
    private String registrationOfficeCode;

    @Column(name = "APPLICATIONNUMBER")
    private String applicationNumber;

    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @Column(name = "DTAPPLICATION")
    private LocalDate applicationDate;

    @Column(name = "REGISTRATIONNUMBER")
    private String registrationNumber;

    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @Column(name = "DTREGISTRATION")
    private LocalDate registrationDate;

    @Column(name = "APPLICATIONREFERENCE")
    private String applicationReference;

    @Column(name = "TYLANGUAGECODE")
    private String applicationLanguageCode;

    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @Column(name = "DTEXPIRY")
    private LocalDate expiryDate;

    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @Column(name = "DTCURRENTSTATUS")
    private LocalDate statusDate;

    @Column(name = "COMMENTS")
    private String comment;

	@Column(name = "DTUPDATE")
    private Date updatedAt;

	@Column(name = "DTUPDATECI", updatable = false, insertable = false)
	private Date updatedAtCi;

}
