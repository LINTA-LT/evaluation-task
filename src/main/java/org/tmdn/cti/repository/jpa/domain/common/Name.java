package org.tmdn.cti.repository.jpa.domain.common;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Data
public class Name {

    @Column(name = "NAMEPREFIX")
    private String namePrefix;

    @Column(name = "FIRSTNAME")
    private String firstName;

    @Column(name = "MIDDLENAME")
    private String middleName;

    @Column(name = "LASTNAME")
    private String lastName;

    @Column(name = "ORGNAME")
    private String organizationName;

    @Column(name = "FREEFORMNAME")
    private String freeFormName;

    public boolean isNotEmpty() {
    	return (!StringUtils.isBlank(firstName) || !StringUtils.isBlank(middleName) || 
    			!StringUtils.isBlank(lastName)  || !StringUtils.isBlank(organizationName));
    }
}
