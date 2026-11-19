package org.tmdn.cti.repository.jpa.domain.trademark;



import lombok.Data;
import org.tmdn.cti.repository.jpa.domain.common.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Table(name = "IMAGECATEGORY")
@Data
public class ImageCategory extends BaseEntity {

    @Id
    @Column(name = "IMAGECATEGORYID")
    private Long id;

    @Column(name = "MARKIMAGEID")
    private Long markImageId;

    @Column(name = "CATEGORYCODE")
    private String categoryCode;

    @Column(name = "INDDOMINANT")
    private Boolean dominantIndicator;

	/**
	 * Validates the content of the category code according to the pattern defined in the 
	 * schema for TM-Search  
	 * @return 
	 */
	public boolean isCategoryCodeValid() {
		Pattern CATEGORY_CODE = Pattern.compile(
				  "[0-9][0-9].[0-9][0-9].[0-9][0-9]|"
				+ "[0-9][0-9].[0-9][0-9]|"
				+ "[0-9][0-9]|"
				+ "[0-9].[0-9].[0-9]|");
		Matcher m = CATEGORY_CODE.matcher(categoryCode);
		if (!m.matches()) {
			throw new IllegalArgumentException("Invalid categoryCode: " + categoryCode);
		}
		return true;
	}
}
