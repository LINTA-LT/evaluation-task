package lt.gov.vpb.pdb.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TmDsPerson extends Person {

    private String freeformAddress750;
    private String country750;

}
