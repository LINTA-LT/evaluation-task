package lt.gov.vpb.pdb.repository.elastic.util.converter;

import lt.gov.vpb.pdb.model.Design;
import lt.gov.vpb.pdb.model.DesignListItem;
import lt.gov.vpb.pdb.model.TmDsPerson;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.join;
import static java.lang.String.valueOf;
import static lt.gov.vpb.pdb.repository.elastic.util.FieldDefinitions.DesignFields.*;
import static lt.gov.vpb.pdb.repository.elastic.util.FieldDefinitions.TrademarkFields.APPLICANTS;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.springframework.util.CollectionUtils.isEmpty;

public class MapToDesignConverter {

    public static List<DesignListItem> convert(List<Map<String, Object>> list) {
        List<DesignListItem> tmList = new ArrayList<>();
        tmList.addAll(list.stream().map(map -> {
            DesignListItem listItem = new DesignListItem();
            listItem.setSt13((String) map.get(ST13));
            listItem.setDescription((String) map.get(DESCRIPTION));
            listItem.setApplicationNumber((String) map.get(APPLICATION_NUMBER));
            listItem.setRegistrationNumber((String) map.get(REGISTRATION_NUMBER));
            List<String> locarnoClassification = (List<String>) map.get(LOCARNO_CLASSIFICATION);
            if(!ObjectUtils.isEmpty(locarnoClassification)){
                listItem.setLocarnoClass(join(", ", locarnoClassification));
            }
            listItem.setExpires(StringToLocalDateConverter.convert(map.get(EXPIRY_DATE)));
            listItem.setStatusLt((String) map.get(STATUS_LT));
            listItem.setStatusEn((String) map.get(STATUS_EN));
            listItem.setNoOfSamples(((Number) map.get(DESIGN_IDENTIFIERS)).intValue());
            List<Map<String, Object>> samplesList = (List<Map<String, Object>>) map.get(SAMPLES);
            listItem.setSamplesExist(!isEmpty(samplesList) && !isEmpty(samplesList.get(0)));
            return listItem;
        }).collect(Collectors.toList()));
        return tmList;
    }

    public static Design convert(Map<String, Object> map) {
        Design design = new Design();
        design.setProductIndication((String) map.get(DESCRIPTION));
        design.setApplicationNumber((String) map.get(APPLICATION_NUMBER));
        design.setRegistrationNumber((String) map.get(REGISTRATION_NUMBER));
        List<String> locarnoClassification = (List<String>) map.get(LOCARNO_CLASSIFICATION);
        if(!ObjectUtils.isEmpty(locarnoClassification)){
            design.setLocarnoClass(join(", ", locarnoClassification));
        }
        design.setFilingDate(StringToLocalDateConverter.convert(map.get(APPLICATION_DATE)));
        design.setRegistrationDate( StringToLocalDateConverter.convert(map.get(REGISTRATION_DATE)));
        design.setPublicationDate( StringToLocalDateConverter.convert(map.get(PUBLICATION_DATE)));
        design.setBulletinNo( (String) map.get(REGISTRATION_PUBLICATION_IDENTIFIER));
        design.setExpires( StringToLocalDateConverter.convert(map.get(EXPIRY_DATE)));
        design.setStatusLt((String) map.get(STATUS_LT));
        design.setStatusEn((String) map.get(STATUS_EN));
        design.setIndRightsPledged(((Number)map.get(INDICATION_RIGHTS_PLEDGED)).intValue() > 0);
        design.setIndRightsArrested(((Number)map.get(INDICATION_RIGHTS_ARRESTED)).intValue() > 0);

        String st13 = (String) map.get(ST13);
        String st13prefix = st13.substring(0, st13.indexOf('-'));
        int noOfSamples = ((Number) map.get(DESIGN_IDENTIFIERS)).intValue();
        design.setSamples(MapToDesignSampleConverter.convert(st13prefix, (List<Map<String, Object>>) map.get(SAMPLES)));

        List<String> images = new ArrayList<>(noOfSamples);
        for (int i = 1; i <= noOfSamples; i++) {
            images.add(st13prefix + "-" + leftPad(valueOf(i), 4, "0"));
        }
        design.setSt13(st13);
        design.setNoOfSamples(noOfSamples);
        design.setImages(images);

        design.setApplicants(MapToTmDsPersonConverter.convert((List<Map<String, Object>>) map.get(APPLICANTS)));
        design.setDisplayApplicants750Field(
                design.getApplicants().stream().anyMatch(person -> person.getFreeformAddress750() != null
                        && !person.getFreeformAddress().equals(person.getFreeformAddress750()))
        );

        for (Map<String, Object> applicantMap : (List<Map<String, Object>>) map.get(APPLICANTS)) {
            if (applicantMap.get("sanctions") != null && (boolean) applicantMap.get("sanctions")) {
                design.setSanctionsApplied(true);
                break;
            }
        }

        List<TmDsPerson> representatives = MapToTmDsPersonConverter.convert((List<Map<String, Object>>) map.get(REPRESENTATIVES));
        if (!isEmpty(representatives)) {
            TmDsPerson person = representatives.get(0);
            design.setRepresentative(person);
            design.setDisplayRepresentative750Field(person.getFreeformAddress750() != null
                && !person.getFreeformAddress().equals(person.getFreeformAddress750()));
        }

        List<Map<String, Object>> conventionPriorities = (List<Map<String, Object>>) map.get(CONVENTION_PRIORITIES);
        if (conventionPriorities != null && !conventionPriorities.get(0).isEmpty()) {
            design.setConventionPriorities(MapToPriorityConverter.convert(conventionPriorities));
        }
        return design;
    }
}
