package lt.gov.vpb.pdb.springbatch.opendata.tm.converters;

import lt.vpb.open_data.schemas.trademark.v1_2.ClassDescriptionType;
import lt.vpb.open_data.schemas.trademark.v1_2.GoodsServicesType;
import lt.vpb.open_data.schemas.trademark.v1_2.GoodsServicesType.ClassDescriptionDetails;
import lt.vpb.open_data.schemas.trademark.v1_2.TextType;
import lt.vpb.open_data.schemas.trademark.v1_2.TradeMarkType.GoodsServicesDetails;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.tmdn.cti.repository.jpa.domain.trademark.TmClassification;

import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Component("opendata.tm.classificationConverter")
public class ClassificationConverter {

    public GoodsServicesDetails convert(List<TmClassification> classificationsList) {
        GoodsServicesDetails goodsServiceDetails = new GoodsServicesDetails();
        ClassDescriptionDetails classDescriptionDetails = new ClassDescriptionDetails();
        for (TmClassification tmClass : classificationsList) {
            ClassDescriptionType classDescriptionType = new ClassDescriptionType();
            classDescriptionType.setClassNumber(tmClass.getMarkClassificationId().getNiceClassNumber());
            TextType goodsServicesDescText = new TextType();
            goodsServicesDescText.setValue(tmClass.getDescription());
            if (StringUtils.isNotBlank(tmClass.getMarkClassificationId().getLanguageCode())) {
                goodsServicesDescText.setLanguageCode(tmClass.getMarkClassificationId().getLanguageCode());
            }
            classDescriptionType.withGoodsServicesDescription(goodsServicesDescText);
            classDescriptionDetails.withClassDescription(classDescriptionType);
        }
        if(isNotEmpty(classDescriptionDetails.getClassDescription())){
            goodsServiceDetails.withGoodsServices(
                    new GoodsServicesType().withClassDescriptionDetails(classDescriptionDetails)
            );
        }
        return goodsServiceDetails;
    }

}
