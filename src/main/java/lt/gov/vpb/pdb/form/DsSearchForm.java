package lt.gov.vpb.pdb.form;

import lombok.Getter;
import lombok.Setter;
import lt.gov.vpb.pdb.model.util.StringMatching;

@Getter
@Setter
public class DsSearchForm extends PaginatedSortedForm {

    private String f54;
    private StringMatching f54_mc;

    private String f11;
    private StringMatching f11_mc;

    private String f21;
    private StringMatching f21_mc;

    private String f22_from;
    private String f22_to;

    public void setF22_from(String value){
        f22_from = value != null ? value.replace('/', '-') : value;
    }

    public void setF22_to(String value){
        f22_to = value != null ? value.replace('/', '-') : value;
    }

}
