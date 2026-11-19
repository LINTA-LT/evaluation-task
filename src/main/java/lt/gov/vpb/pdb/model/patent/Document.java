package lt.gov.vpb.pdb.model.patent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Document extends DocumentListItem{

    private String fileName;
    private byte[] content;

}
