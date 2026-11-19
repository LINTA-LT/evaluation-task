package lt.gov.vpb.pdb.form.patent;

import lombok.Getter;
import lombok.Setter;
import lt.gov.vpb.pdb.model.util.StringMatching;

@Getter
@Setter
public class LtDossierTextSearchForm extends LtDossierSearchForm {

    private String f57;
    private StringMatching f57_mc;

}
