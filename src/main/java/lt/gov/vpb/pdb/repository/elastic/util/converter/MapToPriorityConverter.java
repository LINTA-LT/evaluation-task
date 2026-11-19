package lt.gov.vpb.pdb.repository.elastic.util.converter;

import lt.gov.vpb.pdb.model.Priority;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static lt.gov.vpb.pdb.repository.elastic.util.FieldDefinitions.PriorityFields.*;

public class MapToPriorityConverter {

    public static List<Priority> convert(List<Map<String, Object>> list) {
        List<Priority> priorityList = new ArrayList<>();
        priorityList.addAll(list.stream().map(stringObjectMap -> convert(stringObjectMap)).collect(Collectors.toList()));
        return priorityList;
    }

    public static Priority convert(Map<String, Object> map) {
        Priority priority = new Priority();
        priority.setNumber((String)map.get(PRIORITY_NUMBER));
        priority.setCountryCode((String)map.get(COUNTRY_CODE));
        priority.setDate(StringToLocalDateConverter.convert(map.get(PRIORITY_DATE)));
        priority.setBulletinNr((String)map.get(BULLETIN_NR));
        return priority;
    }
}
