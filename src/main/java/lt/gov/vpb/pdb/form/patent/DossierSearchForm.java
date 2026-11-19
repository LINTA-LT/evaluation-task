package lt.gov.vpb.pdb.form.patent;

import lombok.Getter;
import lombok.Setter;
import lt.gov.vpb.pdb.form.PaginatedSortedForm;
import lt.gov.vpb.pdb.model.util.StringMatching;

/**
 * Dossier (patent domain)
 */
@Getter
@Setter
public class DossierSearchForm extends PaginatedSortedForm {

    private String f11;
    private StringMatching f11_mc;
    
    private String status;

}
