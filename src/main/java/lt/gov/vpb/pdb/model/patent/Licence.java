package lt.gov.vpb.pdb.model.patent;

import lombok.Getter;
import lombok.Setter;
import lt.gov.vpb.pdb.model.Person;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class Licence {

    Long kind;
    LocalDate expiryDate;
    LocalDate agreementRecordDate;
    List<Person> licenciates;

}
