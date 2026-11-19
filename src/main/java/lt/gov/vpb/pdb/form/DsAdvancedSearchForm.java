package lt.gov.vpb.pdb.form;

import lombok.Getter;
import lombok.Setter;
import lt.gov.vpb.pdb.model.util.StringMatching;

@Getter
@Setter
public class DsAdvancedSearchForm extends DsSearchForm {

    //applicant
    private StringMatching f73_name_mc;
    private String f73_name;
    private String f73_address;

    //representative
    private StringMatching f74_name_mc;
    private String f74_name;
    private String f74_address;

    //locarno
    private String f51_1;
    private String f51_2;

    //registration publication
    private String f45_year;
    private String f45_bulletin_no;

}
