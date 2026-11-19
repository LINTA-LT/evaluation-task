package lt.gov.vpb.pdb.springbatch.opendata.tm.converters;


import lt.vpb.open_data.schemas.trademark.v1_2.PriorityType;
import lt.vpb.open_data.schemas.trademark.v1_2.TradeMarkType.PriorityDetails;
import org.springframework.stereotype.Component;
import org.tmdn.cti.repository.jpa.domain.trademark.TmPriority;

import java.util.List;

@Component("opendata.tm.priorityConverter")
public class PriorityConverter {

	public PriorityDetails convert(List<TmPriority> priorities) {

		PriorityDetails priorityDetails = new PriorityDetails();
		PriorityType priorityType;

		for (TmPriority tmPriority : priorities) {
			priorityType = new PriorityType();
			priorityType.setPriorityCountryCode(tmPriority.getPriorityCountryCode());
			priorityType.setPriorityNumber(tmPriority.getPriorityNumber());
			priorityType.setPriorityDate(tmPriority.getPriorityDate());
			priorityDetails.getPriority().add(priorityType);
		}
		return priorityDetails;
	}

}
