package lt.gov.vpb.pdb.springbatch.opendata.tm.converters;

import lt.vpb.open_data.schemas.trademark.v1_2.PublicationSectionType;
import lt.vpb.open_data.schemas.trademark.v1_2.PublicationType;
import lt.vpb.open_data.schemas.trademark.v1_2.TradeMarkType.PublicationDetails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.tmdn.cti.repository.jpa.domain.trademark.TmPublication;

import java.util.List;

@Component("opendata.tm.publicationConverter")
public class PublicationConverter {

	public PublicationDetails convert(List<TmPublication> publicationsList) {

		PublicationDetails publicationDetails = new PublicationDetails();
		PublicationType publicationType;
		for (TmPublication tmPublication : publicationsList) {
			publicationType = new PublicationType();
			if(!StringUtils.isBlank(tmPublication.getPublicationIdentifier())) {
				publicationType.setPublicationIdentifier(tmPublication.getPublicationIdentifier());
			}
			if(!StringUtils.isBlank(tmPublication.getSection())) {
				publicationType.setPublicationSection(convertSectionType(tmPublication.getSection()));
			}
			if(tmPublication.getPublicationDate() != null) {
				publicationType.setPublicationDate(tmPublication.getPublicationDate());
			}
			publicationDetails.getPublication().add(publicationType);
		}
		return publicationDetails;
	}
	
    private static PublicationSectionType convertSectionType(String section) {
		if(section.startsWith("TM_"))
			section = section.substring(3);
		return PublicationSectionType.fromValue(section);
    }
}
