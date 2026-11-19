package lt.gov.vpb.pdb.springbatch.opendata.ds.converters;

import lt.vpb.open_data.schemas.design.v1_2.DesignType;
import lt.vpb.open_data.schemas.design.v1_2.PublicationSectionType;
import lt.vpb.open_data.schemas.design.v1_2.PublicationType;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.tmdn.cti.repository.jpa.converter.AbstractConverter;
import org.tmdn.cti.repository.jpa.domain.design.DesignPublication;

import java.util.List;

@Component
public class PublicationDetailsConverter extends AbstractConverter<DesignPublication, PublicationType, DesignType.PublicationDetails> {

    @Override
    protected DesignType.PublicationDetails wrap(List<PublicationType> list) {
        return new DesignType.PublicationDetails().withPublication(list);
    }

    @Override
    public PublicationType convert(DesignPublication object) {
        PublicationType publicationType = new PublicationType().withPublicationSection(convertSectionType(object.getSection()));
        if (!StringUtils.isBlank(object.getPublicationIdentifier())) {
            publicationType.withPublicationIdentifier(object.getPublicationIdentifier());
        }
        if (object.getPublicationDate() != null) {
            publicationType.withPublicationDate(object.getPublicationDate());
        }
        return publicationType;
    }

    private static PublicationSectionType convertSectionType(String section) {
        if(section.startsWith("DS_"))
            section = section.substring(3);
        return PublicationSectionType.valueOf(section);
    }

}
