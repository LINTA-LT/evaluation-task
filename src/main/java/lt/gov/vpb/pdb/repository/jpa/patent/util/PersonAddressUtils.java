package lt.gov.vpb.pdb.repository.jpa.patent.util;

import lt.gov.vpb.pdb.repository.jpa.patent.domain.EuPersonEntity;
import lt.gov.vpb.pdb.repository.jpa.patent.domain.PersonEntity;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class PersonAddressUtils {


    public static String constructFreeFormAddress(PersonEntity personEntity){
        return constructFreeFormAddress(personEntity.getCity(), personEntity.getAddress(), true, true);
    }

    public static String constructFreeFormAddress(EuPersonEntity personEntity){
        return constructFreeFormAddress(personEntity.getCity(), personEntity.getAddress(),true, true);
    }

    public static String constructFreeFormAddress(String city,
                                                  String streetAddress,
                                                  boolean includeCity,
                                                  boolean includeStreetDetails){
        StringBuilder sbf = new StringBuilder();
        if(!isEmpty(streetAddress) && includeStreetDetails){
            sbf.append(streetAddress);
        }
        if(!isEmpty(city) && includeCity){
            if(sbf.length() > 0){
                sbf.append(", ");
            }
            sbf.append(city);
        }
        return sbf.toString();
    }

}
