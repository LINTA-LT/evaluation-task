package lt.gov.vpb.pdb.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class Design extends Details{

    private String st13;
    private String productIndication;
    private String applicationNumber;
    private LocalDate filingDate;
    private String registrationNumber;
    private LocalDate registrationDate;
    private LocalDate expires;
    private LocalDate publicationDate;
    private String bulletinNo;
    private String statusLt;
    private String statusEn;
    private Boolean indRightsPledged;
    private Boolean indRightsArrested;
    private String locarnoClass;
    private int noOfSamples;
    private List<DesignSample> samples;
    private List<String> images;
    private List<TmDsPerson> applicants;
    private boolean displayApplicants750Field;
    private TmDsPerson representative;
    private boolean displayRepresentative750Field;
    private boolean sanctionsApplied;
    private List<Priority> conventionPriorities;

}

