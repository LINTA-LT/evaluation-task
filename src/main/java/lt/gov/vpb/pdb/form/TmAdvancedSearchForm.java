package lt.gov.vpb.pdb.form;

import lombok.Getter;
import lombok.Setter;
import lt.gov.vpb.pdb.model.util.StringMatching;

@Getter
@Setter
public class TmAdvancedSearchForm extends TmSearchForm{

    // advanced search fields

    //Goods and Services
    private StringMatching f511_class_mc;
    private String f511_class;
    private String f511_list;

    //Class of International Classification of the Figurative Elements of Marks
    private String f531_1;
    private String f531_2;

    //Applicant
    private StringMatching f732_name_mc;
    private String f732_name;
    private String f732_address;

    //Registration publication date
    private String f450_year;
    private String f450_bulletin_no;

    //Application publication date
    private String f442_year;
    private String f442_bulletin_no;

    //Representative
    private StringMatching f740_name_mc;
    private String f740_name;
    private String f740_address;

}
