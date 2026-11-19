package lt.gov.vpb.pdb.model.patent;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Priority {

    private String number;
    private String countryCode;
    private LocalDate date;

}
