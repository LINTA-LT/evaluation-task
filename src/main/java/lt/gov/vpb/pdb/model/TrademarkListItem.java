package lt.gov.vpb.pdb.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Getter
@Setter
public class TrademarkListItem extends ListItem {

    private String trademark;
    private String applicationNumber;
    private String registrationNumber;
    private String tyfeature;
    private String filename;
    private Boolean indSeniority;
    private String classification;
    private LocalDate expires;
    private String statusLt;
    private String statusEn;
    private String st13;

    public boolean isTyfeatureIn(String ... tyfeatures){
        if(isEmpty(tyfeature) || tyfeatures == null || tyfeatures.length ==0){
            return false;
        }
        for(String tyfeatureItem : tyfeatures ){
            if(tyfeature.equals(tyfeatureItem)){
                return true;
            }
        }
        return false;
    }

}
