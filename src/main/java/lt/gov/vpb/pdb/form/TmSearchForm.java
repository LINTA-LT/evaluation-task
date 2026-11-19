package lt.gov.vpb.pdb.form;

import lombok.Getter;
import lombok.Setter;
import lt.gov.vpb.pdb.model.util.StringMatching;

@Getter
@Setter
public class TmSearchForm extends PaginatedSortedForm {

    private String f540;
    private StringMatching f540_mc;

    private String tyfeature;
    private String tykind;
    private String statusEn;
    private String statusLt;

    private String f210;
    private StringMatching f210_mc;

    private String f111;
    private StringMatching f111_mc;

    private String f220_from;
    private String f220_to;

    private String f181_from;
    private String f181_to;

    public void setF220_from(String value){
        f220_from = value != null ? value.replace('/', '-') : value;
    }

    public void setF220_to(String value){
        f220_to = value != null ? value.replace('/', '-') : value;
    }

    public void setf181_from(String value){
        f181_from = value != null ? value.replace('/', '-') : value;
    }

    public void setf181_to(String value){
        f181_to = value != null ? value.replace('/', '-') : value;
    }

}
