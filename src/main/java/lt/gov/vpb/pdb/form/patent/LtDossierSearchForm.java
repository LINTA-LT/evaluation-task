package lt.gov.vpb.pdb.form.patent;

import lombok.Getter;
import lombok.Setter;
import lt.gov.vpb.pdb.model.util.StringMatching;

@Getter
@Setter
public class LtDossierSearchForm extends DossierSearchForm {

    private String f21;
    private StringMatching f21_mc;

    private String status;

}
