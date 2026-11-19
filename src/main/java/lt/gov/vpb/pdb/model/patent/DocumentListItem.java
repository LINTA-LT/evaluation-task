package lt.gov.vpb.pdb.model.patent;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DocumentListItem {

    private Long id;
    private Long type;
    private BigDecimal size;


}
