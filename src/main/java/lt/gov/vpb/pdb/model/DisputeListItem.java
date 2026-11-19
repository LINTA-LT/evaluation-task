package lt.gov.vpb.pdb.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DisputeListItem extends ListItem {

    private boolean international;
    private String applicationNumber;
    private String refApplicationNumber;
    private String registrationNumber;
    private String decision;
    private LocalDate decisionDate;
    private String caseNumber;
    private String contestedTrademark;
    private String niceClassification;
    private String grounds;
    private String status;
}
