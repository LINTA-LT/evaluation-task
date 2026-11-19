package lt.gov.vpb.pdb.repository.elastic.util.converter;

import lt.gov.vpb.pdb.model.NiceClassificationListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static lt.gov.vpb.pdb.repository.elastic.util.FieldDefinitions.NiceClassificationFields.*;

public class MapToNiceClassificationConverter {

        public static List<NiceClassificationListItem> convert(List<Map<String, Object>> list) {
            List<NiceClassificationListItem> classificationList = new ArrayList<>();
            classificationList.addAll(list.stream().map(stringObjectMap -> {
                NiceClassificationListItem niceClassification = new NiceClassificationListItem();
                niceClassification.setClassNumber((Integer)stringObjectMap.get(CLASS_NUMBER));
                niceClassification.setBasicNumber((String)stringObjectMap.get(TERM_NUMBER));
                niceClassification.setClassTermLt((String)stringObjectMap.get(TERM_LT));
                niceClassification.setClassTermEn((String)stringObjectMap.get(TERM_EN));
                return niceClassification;
            }).collect(Collectors.toList()));
            return classificationList;
        }

}
