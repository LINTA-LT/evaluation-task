package org.tmdn.cti.repository.jpa.domain.trademark;



import lombok.Data;
import org.tmdn.cti.repository.jpa.domain.common.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "WORDSPECIFICATION")
@Data
public class WordSpecification extends BaseEntity {
    
	@Id
	@Column(name = "WORDSPECIFICATIONID")
	private Long id;

    @Column(name = "MARKID")
    private Long markId;

    @Column(name = "VERBALELEMENT")
    private String verbalElement;

    @Column(name = "TYLANGCODE")
    private String codeLanguage;

    @Column(name = "TRANSLITERATION")
    private String transliteration;

    @Column(name = "INDSTANDARDCHARACTER")
    private Boolean standardCharIndicator;

}
