package lt.gov.vpb.pdb.repository.elastic.util.converter;

import lt.gov.vpb.pdb.model.DesignSample;

import java.util.*;

import static java.util.Collections.emptyList;
import static lt.gov.vpb.pdb.repository.elastic.util.FieldDefinitions.DesignFields.DESIGN_IDENTIFIER;
import static lt.gov.vpb.pdb.repository.elastic.util.FieldDefinitions.DesignFields.SEQUENCE_NUMBER;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.springframework.util.CollectionUtils.isEmpty;

public class MapToDesignSampleConverter {

    public static List<DesignSample> convert(String st13prefix, List<Map<String, Object>> list) {
        if(isEmpty(list) || (list.size() == 1 && isEmpty(list.get(0)))){
            return emptyList();
        }
        Map<String, DesignSample> samplesMap = new TreeMap<>(Comparator.comparing(Integer::valueOf));
        for (Map<String, Object> map : list) {
            String designIdentifier = String.valueOf(map.get(DESIGN_IDENTIFIER));
            String sequenceNumber = String.valueOf(map.get(SEQUENCE_NUMBER));
            if (!samplesMap.containsKey(designIdentifier)) {
                DesignSample sample = new DesignSample();
                sample.setDesignIdentifier(designIdentifier);
                sample.setImages(new ArrayList<>());
                sample.getImages().add(generateImageIdentifier(st13prefix, designIdentifier, sequenceNumber));
                samplesMap.put(designIdentifier, sample);
            } else {
                DesignSample sample = samplesMap.get(designIdentifier);
                sample.setDesignIdentifier(designIdentifier);
                sample.getImages().add(generateImageIdentifier(st13prefix, designIdentifier, sequenceNumber));
            }
        }
        return new ArrayList<>(samplesMap.values());
    }

    private static String generateImageIdentifier(String st13prefix, String designIdentifier, String sequenceNumber) {
        return st13prefix + "-" + leftPad(designIdentifier, 4, "0") + "-" + leftPad(sequenceNumber, 3, "0");
    }

}
