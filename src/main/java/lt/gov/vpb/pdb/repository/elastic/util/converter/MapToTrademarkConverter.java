package lt.gov.vpb.pdb.repository.elastic.util.converter;

import lt.gov.vpb.pdb.model.TmDsPerson;
import lt.gov.vpb.pdb.model.Trademark;
import lt.gov.vpb.pdb.model.TrademarkListItem;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static lt.gov.vpb.pdb.repository.elastic.util.FieldDefinitions.TrademarkFields.*;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;


public class MapToTrademarkConverter {

    public static List<TrademarkListItem> convert(List<Map<String, Object>> list) {
        List<TrademarkListItem> tmList = new ArrayList<>();
        tmList.addAll(list.stream().map(map -> {
            TrademarkListItem listItem = new TrademarkListItem();
            listItem.setTrademark((String) map.get(DESCRIPTION));
            listItem.setApplicationNumber((String) map.get(APPLICATION_NUMBER));
            listItem.setRegistrationNumber((String) map.get(REGISTRATION_NUMBER));
            listItem.setClassification((String) map.get(NICECLASSNUMBER));
            listItem.setExpires(StringToLocalDateConverter.convert(map.get(EXPIRY_DATE)));
            listItem.setStatusLt((String) map.get(STATUS_LT));
            listItem.setStatusEn((String) map.get(STATUS_EN));
            listItem.setSt13((String) map.get(ST13));
            listItem.setTyfeature((String)map.get(TYFEATURE));
            listItem.setIndSeniority((Boolean) map.get(INDICATION_SENIORITY));
            switch (listItem.getTyfeature()) {
                case TYFEATURE_MULTIMEDIA:
                    listItem.setFilename((String) map.get(MULTIMEDIA_FILENAME));
                    break;
                case TYFEATURE_SOUND:
                    listItem.setFilename((String) map.get(SOUND_FILENAME));
                    break;
                default:
                    String fileName = (String) map.get(IMAGE_FILENAME);
                    if(!isBlank(fileName) && !fileName.endsWith(".null")){
                        listItem.setFilename(fileName);
                    }

            }

            return listItem;
        }).collect(Collectors.toCollection(ArrayList::new)));
        return tmList;
    }

    public static Trademark convert(Map<String, Object> map) {
        Trademark trademark = new Trademark();
        trademark.setTrademark((String) map.get(DESCRIPTION));
        trademark.setApplicationNumber((String) map.get(APPLICATION_NUMBER));
        trademark.setRegistrationNumber((String) map.get(REGISTRATION_NUMBER));
        //TODO
        trademark.setNiceClassification(convertNiceClassification((List<Map<String, Object>>)map.get(CLASSIFICATION)));
        List<String> viennaClassification = new ArrayList<>();
        if ((map.get(VIENNA_CATEGORY_CODE) instanceof Collection<?>)) {
            viennaClassification = (List<String>) map.get(VIENNA_CATEGORY_CODE);
        } else {
            trademark.setViennaClassification((String) map.get(VIENNA_CATEGORY_CODE));
        }
        if(!isEmpty(viennaClassification)){
            trademark.setViennaClassification(join(", ", viennaClassification));
        }
        trademark.setApplicationDate(StringToLocalDateConverter.convert(map.get(APPLICATION_DATE)));
        trademark.setRegistrationDate(StringToLocalDateConverter.convert(map.get(REGISTRATION_DATE)));
        trademark.setApplicationPublicationDate(StringToLocalDateConverter.convert(map.get(APPLICATION_PUBLICATION_DATE)));
        trademark.setApplicationPublicationBulletin((String)map.get(APPLICATION_PUBLICATION_IDENTIFIER));
        trademark.setRegistrationPublicationDate(StringToLocalDateConverter.convert(map.get(REGISTRATION_PUBLICATION_DATE)));
        trademark.setRegistrationPublicationBulletin((String)map.get(REGISTRATION_PUBLICATION_IDENTIFIER));
        trademark.setExpires(StringToLocalDateConverter.convert(map.get(EXPIRY_DATE)));
        trademark.setStatusLt((String) map.get(STATUS_LT));
        trademark.setStatusEn((String) map.get(STATUS_EN));
        trademark.setSt13((String) map.get(ST13));
        trademark.setTyfeature(String.valueOf(map.get(TYFEATURE)));
        trademark.setColourClaimText((String) map.get(COLOUR_CLAIM_TEXT));
        trademark.setDisclaimer((String) map.get(DISCLAIMER));
        trademark.setIndSeniority((Boolean) map.get(INDICATION_SENIORITY));
        if(trademark.getIndSeniority()){
            List<String> ctmSeniorityList = new ArrayList<>(
                    asList(((String) map.get(CTM_SENIORITY))
                            .split(",")))
                    .stream()
                    .map(ctm -> ctm.trim())
                    .collect(toList());
            trademark.setCtmSeniority(ctmSeniorityList);
        }
        switch (trademark.getTyfeature()) {
            case TYFEATURE_MULTIMEDIA:
                trademark.setFilename((String) map.get(MULTIMEDIA_FILENAME));
                break;
            case TYFEATURE_SOUND:
                trademark.setFilename((String) map.get(SOUND_FILENAME));
                break;
            default:
                String fileName = (String) map.get(IMAGE_FILENAME);
                if(!isBlank(fileName) && !fileName.endsWith(".null")){
                    trademark.setFilename(fileName);
                }
        }
        trademark.setIndRightsPledged(((Number)map.get(INDICATION_RIGHTS_PLEDGED)).intValue() > 0);
        trademark.setIndRightsArrested(((Number)map.get(INDICATION_RIGHTS_ARRESTED)).intValue() > 0);

        if (map.get(EXHIBITION_PRIORITIES) != null) {
            trademark.setExhibitionPriorities(MapToExhibitionPriorityConverter.convert((List<Map<String, Object>>) map.get(EXHIBITION_PRIORITIES)));
        }
        if (map.get(PRIORITIES) != null) {
            trademark.setPriorities(MapToPriorityConverter.convert((List<Map<String, Object>>) map.get(PRIORITIES)));
        }
        List<Map<String, Object>> prioritiesSU = (List<Map<String, Object>>) map.get(PRIORITIES_SU);
        if (prioritiesSU != null && !prioritiesSU.get(0).isEmpty()) {
            trademark.setPrioritiesSovietUnion(MapToPriorityConverter.convert(prioritiesSU));
        }
        List<Map<String, Object>> prioritiesEarlier = (List<Map<String, Object>>) map.get(PRIORITIES_EARLIER);
        if (prioritiesEarlier != null && !prioritiesEarlier.get(0).isEmpty()) {
            trademark.setEarlierPriorities(MapToPriorityConverter.convert(prioritiesEarlier));
        }

        if (map.get(APPLICANTS) != null) {
            trademark.setApplicants(MapToTmDsPersonConverter.convert((List<Map<String, Object>>) map.get(APPLICANTS)));
            trademark.setDisplayApplicants750Field(
                    trademark.getApplicants().stream().anyMatch(
                            person -> person.getFreeformAddress750() != null
                                    && !person.getFreeformAddress().equals(person.getFreeformAddress750()))
            );
            for (Map<String, Object> mapas : (List<Map<String, Object>>) map.get(APPLICANTS)) {
                if (mapas.get("sanctions") != null && (boolean) mapas.get("sanctions")) {
                    trademark.setSanctionsApplied(true);
                    break;
                }
            }
        }

        if (map.get(REPRESENTATIVES) != null) {
            List<TmDsPerson> representatives = MapToTmDsPersonConverter.convert((List<Map<String, Object>>) map.get(REPRESENTATIVES));
            if (!isEmpty(representatives)) {
                TmDsPerson person = representatives.get(0);
                trademark.setRepresentative(person);
                trademark.setDisplayRepresentative750Field(person.getFreeformAddress750() != null
                        && !person.getFreeformAddress().equals(person.getFreeformAddress750()));
            }
        }
        return trademark;
    }

    private static List<Trademark.NiceClassification> convertNiceClassification(List<Map<String, Object>> list) {
        List<Trademark.NiceClassification> classificationList = new ArrayList<>();
        classificationList.addAll(list.stream().map(stringObjectMap -> convertNiceClassification(stringObjectMap)).collect(Collectors.toList()));
        return classificationList;
    }

    private static Trademark.NiceClassification convertNiceClassification(Map<String, Object> map) {
        Trademark.NiceClassification niceClassification = new Trademark.NiceClassification();
        niceClassification.setClassNumber((Integer)map.get(NICECLASSNUMBER));
        niceClassification.setDescription((String)map.get(DESCRIPTION));
        return niceClassification;
    }
}
