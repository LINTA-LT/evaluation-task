package lt.gov.vpb.pdb.model.patent;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LicencesList {

    private String patentNumber;
    private String applicationNumber;
    private String titleLt;
    private String titleEn;

    private List<Licence> licences;

}
