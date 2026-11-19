package lt.gov.vpb.pdb.repository.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class MatchingCriteria {

    private String fieldName;
    private String matching;
    private String fieldValue;

}
