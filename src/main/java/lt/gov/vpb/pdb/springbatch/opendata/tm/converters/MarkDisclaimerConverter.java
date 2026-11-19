package lt.gov.vpb.pdb.springbatch.opendata.tm.converters;

import lt.vpb.open_data.schemas.trademark.v1_2.TradeMarkType.MarkDisclaimerDetails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.tmdn.cti.repository.jpa.domain.trademark.Disclaimer;

import java.util.List;

@Component("opendata.tm.markDisclaimerConverter")
public class MarkDisclaimerConverter {
	/**
	 * Converts disclaimer objects coming from table "disclaimer"
	 * into MarkDisclaimerDetails
	 *
	 * @param disclaimers
	 * @return disclaimerDetails
	 */
	public MarkDisclaimerDetails convert(List<Disclaimer> disclaimers) {
		MarkDisclaimerDetails disclaimerDetails = new MarkDisclaimerDetails();
		for (Disclaimer disclaimer : disclaimers) {
			if(StringUtils.isNotBlank(disclaimer.getDescription())) {
				disclaimerDetails.getMarkDisclaimer().add(disclaimer.getDescription());
			}
		}
		return disclaimerDetails;
	}
}
