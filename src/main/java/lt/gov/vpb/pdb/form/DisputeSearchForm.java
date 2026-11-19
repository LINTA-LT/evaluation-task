package lt.gov.vpb.pdb.form;

import lombok.Getter;
import lombok.Setter;
import lt.gov.vpb.pdb.model.util.StringMatching;

import static org.apache.commons.lang3.ArrayUtils.contains;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

@Getter
@Setter
public class DisputeSearchForm extends PaginatedSortedForm{

    public enum DisputeType {

        appeal, opposition, invalidation, cancellation;

        public boolean in(DisputeType ... types){
            return isNotEmpty(types) && contains(types, this);
        }
    }

    private DisputeType type;

    private StringMatching refapplicationnumber_mc;
    private String refapplicationnumber;

    private String date_from;
    private String date_to;

    private StringMatching decision_number_mc;
    private String decision_number;

    private StringMatching f540_mc;
    private String f540;

    private StringMatching f111_mc;
    private String f111;

    private StringMatching f210_mc;
    private String f210;

    private String[] f511;

    private String grounds;
    private String decision;
    private String comments;
    private StringMatching comments_mc;

    public void setDate_from(String value){
        date_from = value != null ? value.replace('/', '-') : value;
    }

    public void setDate_to(String value){
        date_to = value != null ? value.replace('/', '-') : value;
    }
}
