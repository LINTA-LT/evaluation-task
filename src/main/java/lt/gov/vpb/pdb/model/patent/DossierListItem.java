package lt.gov.vpb.pdb.model.patent;

import lombok.Getter;
import lombok.Setter;
import lt.gov.vpb.pdb.model.ListItem;
import lt.gov.vpb.pdb.model.Person;

import java.util.List;

@Getter
@Setter
public class DossierListItem extends ListItem {

    private Long id;
    private Integer type;
    private String applicationNumber;
    private String number;
    private String patentNumber;
    private String titleLt;
    private String titleEn;
    private List<Person> applicants;
    private List<Person> owners;
    private List<Person> attorney;
    private String statusLt;
    private String statusEn;

    private String contentLt;

    public boolean isApplication(){
        return type != null && (type == 0 || type == 11 || type == 22 || type == 23);
    }
}
