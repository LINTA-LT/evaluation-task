package lt.gov.vpb.pdb.repository.elastic.util.converter;

import lt.gov.vpb.pdb.model.Person;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static lt.gov.vpb.pdb.repository.elastic.util.FieldDefinitions.PersonFields.*;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.isEmpty;

public class MapToPersonConverter {

    public static List<Person> convert(List<Map<String, Object>> list) {
        if (isEmpty(list))
            return null;
        List<Person> personsList = new ArrayList<>();
        personsList.addAll(list
                .stream()
                .map(stringObjectMap -> convert(stringObjectMap))
                .filter(person -> person != null)
                .collect(Collectors.toList()));
        return personsList;
    }

    public static Person convert(Map<String, Object> map) {
        if (isEmpty(map))
            return null;
        Person person = new Person();
        person.setFreeformName(!isEmpty(map.get(ORG_NAME)) ? (String)map.get(ORG_NAME) :
                String.format("%s %s %s", map.get(FIRST_NAME), map.get(MIDDLE_NAME), map.get(LAST_NAME)));
        StringBuilder freeformAddress = new StringBuilder()
                .append(map.get(STREET))
                .append(", ")
                .append(StringUtils.isEmpty(map.get(POSTAL_CODE)) ? "" : map.get(POSTAL_CODE))
                .append(" ")
                .append(map.get(CITY));
        person.setFreeformAddress(freeformAddress.toString());
        person.setCountry((String) map.get(COUNTRY_CODE));
        return person;
    }
}
