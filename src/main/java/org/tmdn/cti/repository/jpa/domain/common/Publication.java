package org.tmdn.cti.repository.jpa.domain.common;

import lombok.Data;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
public class Publication extends BaseEntity {

    @Id
    @Column(name = "PUBLICATIONID")
    private Long id;

    @Column(name = "IDENTIFIER")
    private String publicationIdentifier;

    @Column(name = "SECTION")
    private String section;

    @Column(name = "DTPUBLICATION")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate publicationDate;

}
