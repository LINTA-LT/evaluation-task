package lt.gov.vpb.pdb.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DesignListItem extends ListItem{

    private String description;
    private String applicationNumber;
    private String registrationNumber;
    private String locarnoClass;
    private LocalDate expires;
    private String statusEn;
    private String statusLt;
    private String st13;
    private int noOfSamples;
    //extra-indication when images are missing or not; even though designidentifiers > 0
    private boolean samplesExist;

}
