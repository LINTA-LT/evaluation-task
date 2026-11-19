package lt.gov.vpb.pdb.springbatch.opendata.tm.converters;


import lt.vpb.open_data.schemas.trademark.v1_2.TextType;
import lt.vpb.open_data.schemas.trademark.v1_2.WordMarkSpecificationType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.tmdn.cti.repository.jpa.domain.trademark.WordSpecification;

import java.util.List;

@Component("opendata.tm.wordMarkSpecificationConverter")
public class WordMarkSpecificationConverter {

    /**
     * Converts word specification objects coming from table "wordspecification"
     * into WordMarkSpecificationType
     * 
     * @param wordSpecifications
     * @return
     */
    public WordMarkSpecificationType convert(List<WordSpecification> wordSpecifications) {
        WordMarkSpecificationType wordMarkSpec = new WordMarkSpecificationType();
        for (WordSpecification wordSpecification : wordSpecifications) {
            if (StringUtils.isNotBlank(wordSpecification.getVerbalElement())) {
                TextType textType = new TextType();
                textType.setValue(wordSpecification.getVerbalElement());
                wordMarkSpec.setMarkVerbalElementText(textType);
            }
        }
        return wordMarkSpec;
    }
}
