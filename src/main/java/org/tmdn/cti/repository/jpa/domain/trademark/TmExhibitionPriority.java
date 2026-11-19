package org.tmdn.cti.repository.jpa.domain.trademark;

import lombok.Data;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.tmdn.cti.repository.jpa.domain.common.ExhibitionPriority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "EXHIBITIONPRIORITY")
@Data
public class TmExhibitionPriority extends ExhibitionPriority {

	@Column(name = "MARKID")
    private Long markId;

    @Column(name = "TYCOUNTRYCODE")
    private String tyCountryCode;

    @Column(name = "CITY")
    private String city;

}
