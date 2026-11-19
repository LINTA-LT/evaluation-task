package lt.gov.vpb.pdb.model.patent;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class InternationalData {

    private String applicationNumber;
    private LocalDate applicationDate;
    private String applicationPubNumber;
    private LocalDate applicationPubDate;
    private LocalDate intlPatentIssueDate;
    private LocalDate intlPatentIssuePubDate;
    private String countryCode;
}
