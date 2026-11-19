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
public class EuDossier extends Details {

    private String applicationNumber;
    private LocalDate applicationDate;
    private LocalDate applicationPubDate;
    private String number;
    private String statusCode;

    private String titleLt;
    private String titleEn;

    private String statusLt;
    private String statusEn;

    private List<Person> applicants;
    private List<Person> inventors;
    private List<Person> owners;
    private List<Person> representatives;

    private LocalDate invalidationDate;

    // We have dossier(patent) in force in LT,

    private EuPatent patent;

    // or we use data as of EU imported patent

    //f86 f87
    private List<InternationalData> pctData;

    //f96 f97
    private InternationalData epData;

    private LocalDate updatedDate;

    private List<Priority> priorities;
    private List<IPC> ipc;

    private DocumentListItem claimDoc;
    private List<Licence> licences;

    //F45
    LocalDate euPatentIssuancePublicationDate;

    private boolean showNextFee;

    private LocalDate f46Date;

    private Long statusCodeId;

}
