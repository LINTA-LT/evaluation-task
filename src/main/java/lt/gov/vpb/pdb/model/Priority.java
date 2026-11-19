package lt.gov.vpb.pdb.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Priority {

    private String number;
    private String countryCode;
    private LocalDate date;
    private String bulletinNr;

}
