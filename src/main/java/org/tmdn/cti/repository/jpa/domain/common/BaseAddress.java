package org.tmdn.cti.repository.jpa.domain.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseAddress extends BaseEntity{

    @Id
    @Column(name = "ADDRESSID")
    private Long id;

    @Column(name = "STREET")
    private String street;

    @Column(name = "CITY")
    private String city;

    @Column(name = "STATE")
    private String state;

    @Column(name = "TYCOUNTRYCODE")
    private String countryCode;

    @Column(name = "POSTCODE")
    private String postCode;

    @Column(name = "ISDEFAULT")
    private Boolean isDefault;

    @Column(name = "ISACTIVE")
    private Boolean isActive;

    public boolean isNotEmpty(){
    	return !StringUtils.isBlank(getStreet()) || !StringUtils.isBlank(getCity()) ||
        		!StringUtils.isBlank(getPostCode()) || !StringUtils.isBlank(getState());
    }
}
