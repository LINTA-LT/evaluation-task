package lt.gov.vpb.pdb.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NiceClassificationListItem extends ListItem {

    private Integer classNumber;
    private String basicNumber;
    private String classTermLt;
    private String classTermEn;

}
