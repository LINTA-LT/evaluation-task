package org.tmdn.cti.repository.jpa.domain.design;

import lombok.Data;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.tmdn.cti.repository.jpa.domain.common.Filter;
import org.tmdn.cti.repository.jpa.domain.common.IPRight;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "DESIGN")
@Data
public class Design extends IPRight {
	
    @Id
    @Column(name = "DESIGNID")
    private Long id;

    @Column(name = "DESIGNIDENTIFIER")
    private String designIdentifier;

    @Column(name = "DTAPPLICATIONPUBLICATION")
    private Date applicationPublicationDate;

    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @Column(name = "DTPUBLICATION")
    private LocalDate publicationDate;

    @Column(name = "APPLICATIONTYPE")
    private String applicationType;

    @Column(name = "DESCRIPTION")
    private String designDescription;

    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @Column(name = "DTDEFERMENTEXPIRATION")
    private LocalDate defermentExpirationDate;

    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @Column(name = "DTEFFECTIVE")
    private LocalDate effectiveDate;

    @Column(name = "ISINTERNATIONALREGISTRATION")
    private Boolean isInternationalRegistration;
    
    @Column(name = "TYCURRENTSTATUSCODE")
    private String currentStatus;
        
    @OneToMany
    @JoinColumn(name = "DESIGNID")
    private List<DesignPublication> publications;

    @OneToMany
    @JoinColumn(name = "DESIGNID")
    private List<DesignImage> images;

    @OneToMany
    @JoinColumn(name = "DESIGNID")
    private List<DesignPerson> designPersons;

    @OneToMany
    @JoinColumn(name = "DESIGNID")
    private List<IndicationProduct> indicationProducts;

    @OneToMany
    @JoinColumn(name = "DESIGNID")
    private List<DesignClassification> classifications;

    @OneToMany
    @JoinColumn(name = "DESIGNID")
    private List<DesignPriority> priorities;

    @OneToMany
    @JoinColumn(name = "DESIGNID")
    private List<DesignExhibitionPriority> exhibitionPriorities;

    @OneToMany
    @JoinColumn(name = "DESIGNID")
    private List<DesignRecord> records;

    @Transient
    public List<Person> getApplicants() {
        List<Person> applicants = new ArrayList<>();

        if (designPersons != null) {
            for (DesignPerson designPerson : designPersons) {
                if (designPerson.isApplicant() && designPerson.getPerson().isActive() && !applicants.contains(designPerson.getPerson())) {
                    applicants.add(designPerson.getPerson());
                }
            }
        }

        return applicants;
    }

    @Transient
    public List<DesignPersonAddress> getApplicantAddresses() {
        List<DesignPersonAddress> applicantAddresses = new ArrayList<>();

        if (designPersons != null) {
            for (DesignPerson designPerson : designPersons) {
                if (designPerson.isApplicant() && designPerson.isActive()) {
                    applicantAddresses.addAll(designPerson.getDesignPersonAddresses());
                }
            }
        }
        return applicantAddresses;
    }

    @Transient
    public List<Person> getDesigners() {
        List<Person> designers = new ArrayList<Person>();

        if (designPersons != null) {
            for (DesignPerson designPerson : designPersons) {
                if (designPerson.isDesigner() && designPerson.getPerson().isActive() && !designers.contains(designPerson.getPerson())) {
                    designers.add(designPerson.getPerson());
                }
            }
        }

        return designers;
    }

    @Transient
    public List<DesignPersonAddress> getDesignerAddresses() {
        List<DesignPersonAddress> designerAddresses = new ArrayList<>();

        if (designPersons != null) {
            for (DesignPerson designPerson : designPersons) {
                if (designPerson.isDesigner() && designPerson.isActive()) {
                    designerAddresses.addAll(designPerson.getDesignPersonAddresses());
                }
            }
        }
        return designerAddresses;
    }

    @Transient
    public List<Person> getRepresentatives() {
        List<Person> representatives = new ArrayList<Person>();

        if (designPersons != null) {
            for (DesignPerson designPerson : designPersons) {
                if (designPerson.isRepresentative() && designPerson.getPerson().isActive() && !representatives.contains(designPerson.getPerson())) {
                    representatives.add(designPerson.getPerson());
                }
            }
        }

        return representatives;
    }

    @Transient
    public List<DesignPersonAddress> getRepresentativeAddresses() {
        List<DesignPersonAddress> representativeAddresses = new ArrayList<>();

        if (designPersons != null) {
            for (DesignPerson designPerson : designPersons) {
                if (designPerson.isRepresentative() && designPerson.isActive()) {
                    representativeAddresses.addAll(designPerson.getDesignPersonAddresses());
                }
            }
        }
        return representativeAddresses;
    }

    @Transient
    public Person getPreviousApplicant() {
        if (designPersons != null) {
            for (DesignPerson designPerson : designPersons) {
                if (designPerson.isPreviousApplicant() && designPerson.getPerson().isActive()) {
                    return designPerson.getPerson();
                }
            }
        }
        return null;
    }

    public List<DesignRecord> getRecords() {
        return Filter.active(records);
    }

    public List<DesignClassification> getClassifications() {
        return Filter.active(classifications);
    }

    @Transient
    public DesignImage getImage(String image) {
        DesignImage result = null;

        if (images != null) {
            for (DesignImage designImage : images) {
                if (image.equals(String.valueOf(designImage.getId())) && designImage.isActive()) {
                    result = designImage;
                }
            }
        }

        return result;
    }

    public List<DesignImage> getImages() {
        if (images == null) {
            return new ArrayList<DesignImage>();
        } else {
            return Filter.active(images);
        }

    }

}
