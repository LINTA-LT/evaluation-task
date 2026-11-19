package lt.gov.vpb.pdb.model.patent;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class LtPatent extends Dossier {

    private String kind;
    private LocalDate patentPubDate;

    private List<IPC> ipc;

    private List<Priority> priorities;

    private List<InternationalData> pctData;

    //only for PCT
    private LocalDate pctNationalLevelStartDate;

    private List<DocumentListItem> documents;
    private List<DossierListItem> spas;

    private DocumentListItem claimDoc;
    private DocumentListItem abstractEnDoc;
    private DocumentListItem abstractLtDoc;
    private DocumentListItem appSpecificationDoc;
    private DocumentListItem patentSpecificationDoc;
    private DocumentListItem patentSpecificationTextDoc;
    private DocumentListItem searchReportDoc;

    private String contentLt;

    private boolean hasLicences;

}
