package lt.gov.vpb.pdb.repository.elastic.util.converter;

import lt.gov.vpb.pdb.model.TmDsPerson;
import org.springframework.util.StringUtils;

import java.util.*;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static lt.gov.vpb.pdb.repository.elastic.util.FieldDefinitions.PersonFields.*;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.isEmpty;
import static org.springframework.util.StringUtils.trimTrailingCharacter;

public class MapToTmDsPersonConverter {

    public static List<TmDsPerson> convert(List<Map<String, Object>> list) {
        if (isEmpty(list))
            return null;

        Map<String, TmDsPerson> applicants = new HashMap<>();
        list.stream().forEach(stringObjectMap -> convert(stringObjectMap, applicants));
        return new ArrayList<>(applicants.values());
    }

    public static void convert(Map<String, Object> map, Map<String, TmDsPerson> applicants) {
        if (isEmpty(map))
            return;

        String freeformName = !isEmpty(map.get(ORG_NAME))
                ? (String)map.get(ORG_NAME)
                : format("%s %s %s", map.get(FIRST_NAME), map.get(MIDDLE_NAME), map.get(LAST_NAME));

        String freeformAddress = new StringBuilder()
                .append(map.get(STREET))
                .append(", ")
                .append(StringUtils.isEmpty(map.get(POSTAL_CODE)) ? "" : map.get(POSTAL_CODE))
                .append(" ")
                .append(StringUtils.isEmpty(map.get(CITY)) ? "" : map.get(CITY))
                .toString().trim();
        freeformAddress = trimTrailingCharacter(freeformAddress, ',');

        String countryCode = (String) map.get(COUNTRY_CODE);

        boolean isDefault = unknownTypeToBoolean(map.get(ISDEFAULT));
        boolean isPremises = unknownTypeToBoolean(map.get(ISPREMISES));
        boolean isPrimary = unknownTypeToBoolean(map.get(ISPRIMARY));


        TmDsPerson applicant;
        if(applicants.containsKey(freeformName)){
            applicant = applicants.get(freeformName);
        } else {
            applicant = new TmDsPerson();
            applicants.put(freeformName, applicant);
        }

        applicant.setFreeformName(freeformName);
        applicant.setSanctionsApplied(unknownTypeToBoolean(map.get(SANCTIONS)));

        if(isDefault){
            if(applicant.getFreeformAddress() == null) {
                //initialize for now with default address. Will be replaced later if different premises address is found
                applicant.setFreeformAddress(freeformAddress);
                applicant.setCountry(countryCode);
            }
        }
        if(isPremises){
            applicant.setFreeformAddress(freeformAddress);
            applicant.setCountry(countryCode);
        }

        if(isPrimary){
            applicant.setFreeformAddress750(freeformAddress);
            applicant.setCountry750(countryCode);
        }

    }

    private static boolean unknownTypeToBoolean(Object obj){
        if(obj == null){
            return false;
        } else if(obj instanceof Number){
            return ((Number)obj).intValue() == 1;
        } else if (obj instanceof Boolean){
            return ((Boolean)obj).booleanValue();
        } else if (obj instanceof String){
            return ((String)obj).equalsIgnoreCase(TRUE.toString());
        }
        return false;
    }
}
