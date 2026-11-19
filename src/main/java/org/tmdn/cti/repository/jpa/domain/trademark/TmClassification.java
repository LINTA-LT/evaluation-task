package org.tmdn.cti.repository.jpa.domain.trademark;



import lombok.Data;
import org.tmdn.cti.repository.jpa.domain.common.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "CLASSIFICATION")
@Data
public class TmClassification extends BaseEntity {

	@EmbeddedId
    private TmClassificationId markClassificationId;

    @Column(name = "DESCRIPTION")
	@Lob
	private String description;

    @Column(name = "TYSTATUSCODE")
    private String statusCode;

    @Column(name = "VERSION")
    private String version;

}
