package lt.gov.vpb.pdb.model.patent;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * EP which became effective in LT.
 */
@Getter
@Setter
public class EuPatent extends Dossier {

    //f86 f87
    private List<InternationalData> pctData;

    //f96 f97
    private InternationalData epData;

    private List<Priority> priorities;
    private List<IPC> ipc;
    private List<DossierListItem> spas;

    private DocumentListItem claimDoc;

    //whether has licences
    private boolean hasLicences;

    private String epoStatus;

}
