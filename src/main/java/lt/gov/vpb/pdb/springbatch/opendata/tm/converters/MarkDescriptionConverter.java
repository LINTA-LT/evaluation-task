package lt.gov.vpb.pdb.springbatch.opendata.tm.converters;

import lt.vpb.open_data.schemas.trademark.v1_2.TradeMarkType.MarkDescriptionDetails;
import org.springframework.stereotype.Component;
import org.tmdn.cti.repository.jpa.domain.trademark.TmDescription;

import java.util.List;

@Component("opendata.tm.markDescriptionConverter")
public class MarkDescriptionConverter {

	/**
	 * Converts mark description objects coming from table "markdescription"
	 * into MarkDescriptionDetails
	 *
	 * @param tmDescriptionlist
	 * @return markDescriptionsDetails
	 */
	public MarkDescriptionDetails convert(List<TmDescription> tmDescriptionlist) {
		MarkDescriptionDetails markDescriptionDetails = new MarkDescriptionDetails();
		for (TmDescription description : tmDescriptionlist) {
			markDescriptionDetails.getMarkDescription().add(description.getDescription());
		}
		return markDescriptionDetails;
	}
}
