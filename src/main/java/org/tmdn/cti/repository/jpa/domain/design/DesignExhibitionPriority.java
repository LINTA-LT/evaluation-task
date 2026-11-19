package org.tmdn.cti.repository.jpa.domain.design;

import lombok.Data;
import org.tmdn.cti.repository.jpa.domain.common.ExhibitionPriority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "EXHIBITIONPRIORITY")
@Data
public class DesignExhibitionPriority extends ExhibitionPriority {

	@Column(name = "DESIGNID")
    private Long designId;

    @Column(name = "DTFIRSTDISPLAY")
    private Date exhibitionFirstDisplayDate;
    
}
