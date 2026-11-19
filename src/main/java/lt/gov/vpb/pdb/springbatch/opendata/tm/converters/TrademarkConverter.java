package lt.gov.vpb.pdb.springbatch.opendata.tm.converters;

import lombok.extern.slf4j.Slf4j;
import lt.gov.vpb.pdb.springbatch.opendata.IPRightType;
import lt.vpb.open_data.schemas.iso639_language_code.v1.ISO639LanguageCodeType;
import lt.vpb.open_data.schemas.trademark.v1_2.KindMarkType;
import lt.vpb.open_data.schemas.trademark.v1_2.MarkCurrentStatusCodeType;
import lt.vpb.open_data.schemas.trademark.v1_2.TradeMarkType;
import lt.vpb.open_data.schemas.trademark.v1_2.TradeMarkType.PriorityDetails;
import lt.vpb.open_data.schemas.trademark.v1_2.WordMarkSpecificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tmdn.cti.exception.ConversionError;
import org.tmdn.cti.repository.jpa.domain.trademark.TradeMark;

import java.util.List;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

@Component
@Slf4j
public class TrademarkConverter {

    private static final int FIRST_ELEMENT = 0;

    @Autowired
    private ImageConverter imageConverter;

    @Autowired
    private ClassificationConverter classificationConverter;

    @Autowired
    private PriorityConverter priorityConverter;

    @Autowired
    private PersonConverter personConverter;

    @Autowired
    private PublicationConverter publicationConverter;

    @Autowired
    private ExhibitionPriorityConverter exhibitionPriorityConverter;

    @Autowired
    private MarkDisclaimerConverter markDisclaimerConverter;

    @Autowired
    private MarkDescriptionConverter markDescriptionConverter;

    @Autowired
    private WordMarkSpecificationConverter wordMarkSpecificationConverter;

    @Autowired
    private SeniorityDetailsConverter seniorityDetailsConverter;

    public TradeMarkType convert(TradeMark trademark, List<ConversionError> errors) {
        if (trademark == null) {
            return null;
        }
        try {
            return convert(trademark);
        } catch (IllegalArgumentException exception) {
            log.error("An error occured during the convertion of db data for trademark with application number {}", trademark.getApplicationNumber());
            log.error("Invalid value {} for field {} ", exception.getMessage(), exception.getStackTrace()[0].getFileName());
            errors.add(new ConversionError(trademark.getApplicationNumber(), format("Invalid value %s", exception.getMessage()),
                    IPRightType.TRADEMARK));
            return null;
        }
    }

    public TradeMarkType convert(TradeMark object) {
        TradeMarkType tmType = new TradeMarkType();
        fillMarkSimpletypes(object, tmType);
        fillComplexTypes(object, tmType);
        return tmType;
    }

    /**
     * Fills complex type elements with information retrieved from tables associated
     * to the trademark information
     * 
     * @param trademark
     * @param tradeMarkType
     */
    private void fillComplexTypes(TradeMark trademark, TradeMarkType tradeMarkType) {
        if (!isEmpty(trademark.getMarkDescriptions())) {
            tradeMarkType.setMarkDescriptionDetails(markDescriptionConverter.convert(trademark.getMarkDescriptions()));
        }
        if (!isEmpty(trademark.getDisclaimers())) {
            TradeMarkType.MarkDisclaimerDetails disclaimerDetails = markDisclaimerConverter.convert(trademark.getDisclaimers());
            if (!disclaimerDetails.getMarkDisclaimer().isEmpty()) {
                tradeMarkType.setMarkDisclaimerDetails(disclaimerDetails);
            }
        }
        if (!isEmpty(trademark.getWordSpecifications())) {
            WordMarkSpecificationType wordMarkSpecification = wordMarkSpecificationConverter
                    .convert(trademark.getWordSpecifications());
            if (wordMarkSpecification.getMarkVerbalElementText() != null) {
                tradeMarkType.setWordMarkSpecification(wordMarkSpecification);
            }
        }
        if (!isEmpty(trademark.getImages())) {
            tradeMarkType.setMarkImageDetails(imageConverter.convert(trademark.getImages().get(FIRST_ELEMENT), trademark.getApplicationNumber()));
        }
        if (trademark.getClassifications() != null) {
            tradeMarkType.setGoodsServicesDetails(classificationConverter.convert(trademark.getClassifications()));
        }
        if (trademark.getPriorities() != null) {
            PriorityDetails priorityDetails = priorityConverter.convert(trademark.getPriorities());
            if (!priorityDetails.getPriority().isEmpty()) {
                tradeMarkType.setPriorityDetails(priorityDetails);
            }
        }
        if (!isEmpty(trademark.getExhibitionPriorities())) {
            tradeMarkType.setExhibitionPriorityDetails(
                    exhibitionPriorityConverter.convert(trademark.getExhibitionPriorities()));
        }
        if (!isEmpty(trademark.getPublications())) {
            tradeMarkType.setPublicationDetails(publicationConverter.convert(trademark.getPublications()));
        }
        if (!isEmpty(trademark.getApplicants())) {
            tradeMarkType.setApplicantDetails(personConverter.convertApplicants(trademark));
        }
        if (!isEmpty(trademark.getRepresentatives())) {
            tradeMarkType.setRepresentativeDetails(personConverter.convertRepresentatives(trademark));
        }
    }

    /**
     * Fills simple type elements with mark table information
     * 
     * @param trademark
     * @param tradeMarkType
     */
    private void fillMarkSimpletypes(TradeMark trademark, TradeMarkType tradeMarkType) {
        tradeMarkType.setApplicationNumber(trademark.getApplicationNumber());
        tradeMarkType.setApplicationDate(trademark.getApplicationDate());
        if (!isBlank(trademark.getRegistrationNumber())) {
            tradeMarkType.setRegistrationNumber(trademark.getRegistrationNumber());
        }
        tradeMarkType.setRegistrationDate(trademark.getRegistrationDate());
        if (!isBlank(trademark.getKindOfMark())) {
            tradeMarkType.setKindMark(KindMarkTypeConverter.convert(trademark.getKindOfMark()));
        }
        if (!isBlank(trademark.getApplicationLanguageCode())) {
            tradeMarkType.setApplicationLanguageCode(
                    ISO639LanguageCodeType.fromValue(trademark.getApplicationLanguageCode().toLowerCase()));
        }
        tradeMarkType.setExpiryDate(trademark.getExpiryDate());
        tradeMarkType.setMarkCurrentStatusCode(MarkCurrentStatusCodeType.fromValue(trademark.getCurrentStatus().getDescription()));
        tradeMarkType.setMarkCurrentStatusDate(trademark.getStatusDate());
        if (!isBlank(trademark.getKindOfMark())) {
            tradeMarkType.setMarkFeature(trademark.getKindOfMark());
        }
        tradeMarkType.setMarkFeature(trademark.getMarkFeature());
        tradeMarkType.setOppositionPeriodStartDate(trademark.getOppositionStartDate());
        tradeMarkType.setOppositionPeriodEndDate(trademark.getOppositionEndDate());
        // Currently seniorities are empty, so we populate ctm number instead
        tradeMarkType.setSeniorityDetails(seniorityDetailsConverter.convert(trademark.getCtmNumber()));
    }
}
