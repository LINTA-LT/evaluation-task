package lt.gov.vpb.pdb.form;

import lombok.Getter;
import lombok.Setter;
import lt.gov.vpb.pdb.model.util.StringMatching;

@Getter
@Setter
public class NiceClassificationSearchForm extends PaginatedSortedForm {

    public NiceClassificationSearchForm(){
        super.setSize(100);
    }

    private Integer class_number;
    private String first_letter;
    private String class_term_lt;
    private StringMatching class_term_lt_mc;
    private String class_term_en;
    private StringMatching class_term_en_mc;
    private String basic_number;
    private StringMatching basic_number_mc;
    private Integer version_year;

}
