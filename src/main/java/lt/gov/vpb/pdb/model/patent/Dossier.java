package lt.gov.vpb.pdb.model.patent;

import lombok.Getter;
import lombok.Setter;
import lt.gov.vpb.pdb.model.Details;
import lt.gov.vpb.pdb.model.Person;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class Dossier extends Details {

    private String applicationNumber;
    private Integer type;
    private LocalDate applicationDate;
    private LocalDate applicationPubDate;
    private String number;

    private String titleLt;
    private String titleEn;

    private String statusLt;
    private String statusEn;

    private LocalDate updatedDate;

    private List<Person> applicants;
    private List<Person> inventors;
    private List<Person> owners;
    private List<Person> representatives;

    private LocalDate lastFeeDate;
    private Long lastFeeValidityYears;
    private Long lastFeeAmount;
    private String lastFeeCurrency;
    private LocalDate nextFeeDate;

    private LocalDate invalidationDate;

    private Boolean showNextFee;

}
