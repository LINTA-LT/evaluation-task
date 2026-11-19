package lt.gov.vpb.pdb.model.patent;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SPA  extends Dossier {

    private String patentNumber;
    private String parentApplicationNumber;

    private String f92Number;
    private LocalDate f92Date;

    private String f93Number;
    private LocalDate f93Date;
    private LocalDate f93PubDate;

    //F24 spc effective date
    private LocalDate spcEffectiveDate;
    //F94 spc expiry date
    private LocalDate spcExpiryDate;

    //F41 spc application publication date
    private LocalDate spcApplicationPubDate;

    //F95 protected product name
    private String protectedProductName;

    //Paediatric extension properties
    private LocalDate extensionApplicationDate;
    private LocalDate extensionExpiryDate;

    //spc publication date
    private LocalDate spcPublicationDate;
}
