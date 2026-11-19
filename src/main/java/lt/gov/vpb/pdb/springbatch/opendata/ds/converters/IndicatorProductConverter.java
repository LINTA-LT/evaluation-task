package lt.gov.vpb.pdb.springbatch.opendata.ds.converters;

import lt.vpb.open_data.schemas.design.v1_2.DesignType;
import lt.vpb.open_data.schemas.design.v1_2.TextType;
import org.springframework.stereotype.Component;
import org.tmdn.cti.repository.jpa.converter.AbstractConverter;
import org.tmdn.cti.repository.jpa.domain.design.IndicationProduct;

import java.math.BigInteger;
import java.util.List;

@Component
public class IndicatorProductConverter extends AbstractConverter<IndicationProduct, TextType, DesignType.IndicationProductDetails> {

    @Override protected DesignType.IndicationProductDetails wrap(List<TextType> list) {
        return new DesignType.IndicationProductDetails().withIndicationProduct(list);
    }

    @Override public TextType convert(IndicationProduct indicationProduct) {
        TextType textType = new TextType()
                .withLanguageCode(indicationProduct.getLanguageCode())
                .withValue(indicationProduct.getDescription());

        if (indicationProduct.getSequenceNumber() != null) {
            textType.setSequenceNumber(BigInteger.valueOf(indicationProduct.getSequenceNumber()));
        }

        return textType;
    }
}
