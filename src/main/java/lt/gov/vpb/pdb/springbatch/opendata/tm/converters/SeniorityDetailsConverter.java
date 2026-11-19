package lt.gov.vpb.pdb.springbatch.opendata.tm.converters;

import lt.vpb.open_data.schemas.trademark.v1_2.TradeMarkType;
import lt.vpb.open_data.schemas.trademark.v1_2.TradeMarkType.SeniorityDetails;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Component("opendata.tm.seniorityDetailsConverter")
public class SeniorityDetailsConverter {

    public SeniorityDetails convert(String ctmNumber) {
        if (isEmpty(ctmNumber)) {
            return null;
        }
        TradeMarkType.SeniorityDetails seniorityDetails = new TradeMarkType.SeniorityDetails();
        String[] ctmNumbers = ctmNumber.split(",");
        for (String ctmNumberItem : ctmNumbers) {
            seniorityDetails.getSeniorityBeenClaimedNumber().add(ctmNumberItem.trim());
        }
        return seniorityDetails;

    }

}
