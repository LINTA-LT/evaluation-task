package lt.gov.vpb.pdb.form.patent;

import lombok.Getter;
import lombok.Setter;
import lt.gov.vpb.pdb.model.util.StringMatching;

@Getter
@Setter
public class LtDossierAdvancedSearchForm extends LtDossierSearchForm {

    private String f51;
    private StringMatching f51_mc;

    private String f71_name;
    private StringMatching f71_name_mc;
    private String f71_address;
    private String f71_country;

    private String f72_name;
    private StringMatching f72_name_mc;
    private String f72_country;

    private String f73_name;
    private StringMatching f73_name_mc;
    private String f73_address;
    private String f73_country;

    private String f74_name;
    private StringMatching f74_name_mc;
    private String f74_address;

    private String f54;
    private StringMatching f54_mc;

    private String f57;
    private StringMatching f57_mc;

    private String f30_number;
    private StringMatching f30_number_mc;
    private String f30_date;

    private String f86_appnumber;
    private StringMatching f86_appnumber_mc;
    private String f86_appdate;

    private String f87_pubnumber;
    private StringMatching f87_pubnumber_mc;
    private String f87_pubdate;

    private String spa;
    private StringMatching spa_mc;

    private String spa_f95;
    private StringMatching spa_f95_mc;

}
