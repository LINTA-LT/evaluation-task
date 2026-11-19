package lt.gov.vpb.pdb.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DesignSample {

    private String st13prefix;
    //design identifier
    private String designIdentifier;
    //design images (format: st13-identifier-sequence)
    private List<String> images;

}
