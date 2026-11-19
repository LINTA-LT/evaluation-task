package org.tmdn.cti.repository.jpa.domain.trademark;

import lombok.Data;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.tmdn.cti.repository.jpa.domain.common.IPRight;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "MARK")
@Data
public class TradeMark extends IPRight {

    @Id
    @Column(name = "MARKID")
    private Long id;

    @Column(name = "FILINGPLACE")
    private String filingPlace;

    @Column(name = "TYIPRKIND")
    private String ipKind;

    @Column(name = "TYKIND")
    private String kindOfMark;

    @Column(name = "TYFEATURE")
    private String markFeature;

    @Column(name = "DTOPPOSITIONSTART")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate oppositionStartDate;

    @Column(name = "DTOPPOSITIONEND")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate oppositionEndDate;

    @Column(name = "INDSENIORITY")
    private Boolean seniorityIndicator;

    @Column(name = "NRCTMNUMBER")
    private String ctmNumber;

    @ManyToOne
    @JoinColumn(name = "TYCURRENTSTATUSCODE")
    private MarkStatus currentStatus;

    @OneToMany
    @JoinColumn(name = "MARKID")
    private List<TmPublication> publications;

    @OneToMany
    @JoinColumn(name = "MARKID")
    private List<TmImage> images;

    @OneToMany
    @JoinColumn(name = "MARKID")
    private List<TmSound> sounds;

    @OneToMany
    @JoinColumn(name = "MARKID")
    private List<TmVideo> videos;

    @OneToMany
    @JoinColumn(name = "MARKID")
    private List<MarkPerson> markPersons;

    @OneToMany
    @JoinColumn(name = "MARKID")
    private List<TmDescription> markDescriptions;

    @OneToMany
    @JoinColumn(name = "MARKID")
    private List<TmExhibitionPriority> exhibitionPriorities;

    @OneToMany
    @JoinColumn(name = "MARKID")
    private List<TmPriority> priorities;

    @OneToMany
    @JoinColumn(name = "MARKID")
    private List<TmClassification> classifications;

    @OneToMany
    @JoinColumn(name = "MARKID")
    private List<Disclaimer> disclaimers;

    @OneToMany
    @JoinColumn(name = "MARKID")
    private List<WordSpecification> wordSpecifications;

    @OneToMany
    @JoinColumn(name = "MARKID")
    private List<Opposition> oppositions;

    @OneToMany
    @JoinColumn(name = "MARKID")
    private List<MarkRecord> records;

    @Transient
    public List<Person> getApplicants() {
        Set<Person> applicants = new HashSet<>();

        if (markPersons != null) {
            for (MarkPerson markPerson : markPersons) {
                if (markPerson.isApplicant() && markPerson.getPerson().isActive()) {
                    applicants.add(markPerson.getPerson());
                }
            }
        }
        return new ArrayList<>(applicants);
    }

    @Transient
    public List<MarkPersonAddress> getApplicantAddresses() {
        List<MarkPersonAddress> applicantAddresses = new ArrayList<>();

        if (markPersons != null) {
            for (MarkPerson markPerson : markPersons) {
                if (markPerson.isApplicant() && markPerson.isActive()) {
                    applicantAddresses.addAll(markPerson.getMarkPersonAddresses());
                }
            }
        }
        return applicantAddresses;
    }

    @Transient
    public List<Person> getRepresentatives() {
        List<Person> representatives = new ArrayList<Person>();

        if (markPersons != null) {
            for (MarkPerson markPerson : markPersons) {
                if (markPerson.isRepresentative() && markPerson.getPerson().isActive() && !representatives.contains(markPerson.getPerson())) {
                    representatives.add(markPerson.getPerson());
                }
            }
        }
        return representatives;
    }

    @Transient
    public List<MarkPersonAddress> getRepresentativeAddresses() {
        List<MarkPersonAddress> representativeAddresses = new ArrayList<>();

        if (markPersons != null) {
            for (MarkPerson markPerson : markPersons) {
                if (markPerson.isRepresentative() && markPerson.getPerson().isActive()) {
                    representativeAddresses.addAll(markPerson.getMarkPersonAddresses());
                }
            }
        }
        return representativeAddresses;
    }
}
