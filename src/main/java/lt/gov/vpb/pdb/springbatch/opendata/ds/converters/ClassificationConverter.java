package lt.gov.vpb.pdb.springbatch.opendata.ds.converters;


import lt.vpb.open_data.schemas.design.v1_2.ClassDescriptionType;
import lt.vpb.open_data.schemas.design.v1_2.DesignType.ClassDescriptionDetails;
import org.springframework.stereotype.Component;
import org.tmdn.cti.repository.jpa.converter.AbstractConverter;
import org.tmdn.cti.repository.jpa.domain.design.DesignClassification;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.leftPad;

@Component
public class ClassificationConverter extends AbstractConverter<DesignClassification, ClassDescriptionType, ClassDescriptionDetails> {

    public static final int CATEGORY_LENGTH = 2;

    public static final String CATEGORY_PADDING = "0";

    @Override
    protected ClassDescriptionDetails wrap(List<ClassDescriptionType> list) {
        return new ClassDescriptionDetails().withClassDescription(list);
    }

    @Override
    public ClassDescriptionType convert(DesignClassification object) {
        String[] classNumber = object.getClassNumber().split("\\.");
        if (classNumber.length > 1) {
            return new ClassDescriptionType().withClassNumber(
                    leftPad(classNumber[0], CATEGORY_LENGTH, CATEGORY_PADDING)
                            + "."
                            + leftPad(classNumber[1], CATEGORY_LENGTH, CATEGORY_PADDING)
            );
        } else {
            return new ClassDescriptionType().withClassNumber(leftPad(classNumber[0], CATEGORY_LENGTH, CATEGORY_PADDING));
        }

    }
}
