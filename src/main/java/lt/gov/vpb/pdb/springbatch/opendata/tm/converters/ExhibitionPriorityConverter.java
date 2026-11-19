package lt.gov.vpb.pdb.springbatch.opendata.tm.converters;

import lt.vpb.open_data.schemas.trademark.v1_2.ExhibitionPriorityType;
import lt.vpb.open_data.schemas.trademark.v1_2.TradeMarkType.ExhibitionPriorityDetails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.tmdn.cti.repository.jpa.domain.trademark.TmExhibitionPriority;

import java.util.List;

@Component("opendata.tm.exhibitionPriorityConverter")
public class ExhibitionPriorityConverter {

    public ExhibitionPriorityDetails convert(List<TmExhibitionPriority> exhibitionPriorityList) {
        ExhibitionPriorityDetails exhibitionPriorityDetails = new ExhibitionPriorityDetails();
        ExhibitionPriorityType exhibitionPriorityType;
        for (TmExhibitionPriority tmExhibitionPriority : exhibitionPriorityList) {
            exhibitionPriorityType = new ExhibitionPriorityType();
            if (!StringUtils.isBlank(tmExhibitionPriority.getExhibitionName())) {
                exhibitionPriorityType.setExhibitionName(tmExhibitionPriority.getExhibitionName());
            }
            if (tmExhibitionPriority.getExhibitionDate() != null) {
                exhibitionPriorityType.setExhibitionDate(tmExhibitionPriority.getExhibitionDate());
            }
            if (tmExhibitionPriority.getTyCountryCode() != null) {
                exhibitionPriorityType.setExhibitionCountryCode(tmExhibitionPriority.getTyCountryCode());
            }
            exhibitionPriorityDetails.getExhibitionPriority().add(exhibitionPriorityType);
        }
        return exhibitionPriorityDetails;
    }
}
