package lt.gov.vpb.pdb.repository.elastic.util.converter;

import lt.gov.vpb.pdb.model.Dispute;
import lt.gov.vpb.pdb.model.DisputeListItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.join;
import static lt.gov.vpb.pdb.repository.elastic.util.FieldDefinitions.DisputeFields.*;

public class MapToDisputeConverter {

    public static List<DisputeListItem> convert(List<Map<String, Object>> list) {
        List<DisputeListItem> tmList = new ArrayList<>();
        tmList.addAll(list.stream().map(map -> {
            DisputeListItem listItem = new DisputeListItem();
            listItem.setRefApplicationNumber((String) map.get(REF_APPLICATION_NUMBER));
            listItem.setInternational((Boolean) map.get(IND_INTERNATIONAL));
            listItem.setApplicationNumber((String) map.get(APPLICATION_NUMBER));
            listItem.setRegistrationNumber((String) map.get(REGISTRATION_NUMBER));
            listItem.setDecision((String) map.get(DECISION_TAKEN));
            listItem.setDecisionDate(StringToLocalDateConverter.convert(map.get(DECISION_EFFECTIVE_DATE)));
            listItem.setCaseNumber((String) map.get(CASE_NUMBER));
            listItem.setContestedTrademark((String) map.get(VERBAL_ELEMENT));
            if(map.get(NICE_CLASSIFICATION) != null){
                listItem.setNiceClassification(join(", ", (List) map.get(NICE_CLASSIFICATION)));
            }
            if(map.get(GROUNDS) != null){
                //duplicates may occur, include a fix via HashSet
                listItem.setGrounds(join(", ", new HashSet<>((List) map.get(GROUNDS))));
            }
            listItem.setStatus((String) map.get(CURRENT_STATUS));
            return listItem;
        }).collect(Collectors.toList()));
        return tmList;
    }

    public static Dispute convert(Map<String, Object> map) {
        Dispute opposition = new Dispute();
        opposition.setApplicationNumber((String) map.get(APPLICATION_NUMBER));
        opposition.setDecisionComments((String) map.get(COMMENTS));
        return opposition;
    }
}
