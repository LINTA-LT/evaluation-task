package lt.gov.vpb.pdb.springbatch.opendata.ds.converters;


import lt.vpb.open_data.schemas.design.v1_2.DesignType.PriorityDetails;
import lt.vpb.open_data.schemas.design.v1_2.PriorityType;
import org.springframework.stereotype.Component;
import org.tmdn.cti.repository.jpa.converter.AbstractConverter;
import org.tmdn.cti.repository.jpa.domain.design.DesignPriority;

import java.util.List;

@Component
public class PriorityDetailsConverter extends AbstractConverter<DesignPriority, PriorityType, PriorityDetails> {

    @Override
    protected PriorityDetails wrap(List<PriorityType> list) {
        return new PriorityDetails().withPriority(list);
    }

    @Override
    public PriorityType convert(DesignPriority object) {
        return new PriorityType()
                .withPriorityCountryCode(object.getPriorityCountryCode())
                .withPriorityDate(object.getPriorityDate())
                .withPriorityNumber(object.getPriorityNumber());

    }
}
