package lt.gov.vpb.pdb.repository.elastic.util.converter;

import lt.gov.vpb.pdb.model.ExhibitionPriority;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static lt.gov.vpb.pdb.repository.elastic.util.FieldDefinitions.ExibitionPriorityFields.COUNTRY_CODE;
import static lt.gov.vpb.pdb.repository.elastic.util.FieldDefinitions.ExibitionPriorityFields.PRIORITY_DATE;

public class MapToExhibitionPriorityConverter {

    public static List<ExhibitionPriority> convert(List<Map<String, Object>> list) {
        List<ExhibitionPriority> priorityList = new ArrayList<>();
        priorityList.addAll(list.stream().map(stringObjectMap -> convert(stringObjectMap)).collect(Collectors.toList()));
        return priorityList;
    }

    public static ExhibitionPriority convert(Map<String, Object> map) {
        ExhibitionPriority priority = new ExhibitionPriority();
        priority.setCountryCode((String)map.get(COUNTRY_CODE));
        priority.setDate(StringToLocalDateConverter.convert(map.get(PRIORITY_DATE)));
        return priority;
    }
}
