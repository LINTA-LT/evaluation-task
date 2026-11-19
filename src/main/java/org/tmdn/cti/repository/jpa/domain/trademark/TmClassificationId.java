package org.tmdn.cti.repository.jpa.domain.trademark;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class TmClassificationId implements Serializable{
    
	@Column(name = "MARKID")
    private Long markId;

	@Column(name = "NICECLASSNUMBER")
    private int niceClassNumber;

	@Column(name = "TYLANGCODE")
    String languageCode;

}
