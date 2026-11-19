package lt.gov.vpb.pdb.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Getter
@Setter
public class Trademark extends Details {

    @Getter
    @Setter
    public static class NiceClassification {

        private String description;
        private Integer classNumber;

    }

    private String st13;
    private String trademark;
    private String applicationNumber;
    private LocalDate applicationDate;
    private String registrationNumber;
    private LocalDate registrationDate;
    private LocalDate expires;
    private LocalDate applicationPublicationDate;
    private String applicationPublicationBulletin;
    private LocalDate registrationPublicationDate;
    private String registrationPublicationBulletin;
    private String statusLt;
    private String statusEn;
    private String tyfeature;
    private String viennaClassification;
    private List<Trademark.NiceClassification> niceClassification;
    private String colourClaimText;
    private List<TmDsPerson> applicants;
    private boolean displayApplicants750Field;
    private TmDsPerson representative;
    private boolean displayRepresentative750Field;
    private List<ExhibitionPriority> exhibitionPriorities;
    private List<Priority> priorities;
    private List<Priority> prioritiesSovietUnion;
    private List<Priority> earlierPriorities;
    private String disclaimer;
    private String filename;
    private List<String> ctmSeniority;
    private Boolean indSeniority;
    private Boolean indRightsPledged;
    private Boolean indRightsArrested;
    private boolean sanctionsApplied;

    public boolean isTyfeatureIn(String ... tyfeatures){
        if(isEmpty(tyfeature) || tyfeatures == null || tyfeatures.length ==0){
            return false;
        }
        for(String tyfeatureItem : tyfeatures ){
            if(tyfeature.equals(tyfeatureItem)){
                return true;
            }
        }
        return false;
    }

}
