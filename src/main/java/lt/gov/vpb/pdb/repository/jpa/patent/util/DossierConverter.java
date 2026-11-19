package lt.gov.vpb.pdb.repository.jpa.patent.util;

import lt.gov.vpb.pdb.model.Person;
import lt.gov.vpb.pdb.model.patent.*;
import lt.gov.vpb.pdb.repository.jpa.patent.domain.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Locale.ENGLISH;
import static lt.gov.vpb.pdb.repository.jpa.patent.util.PersonAddressUtils.constructFreeFormAddress;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.springframework.util.StringUtils.parseLocale;

@Component
public class DossierConverter {

    @Autowired
    private MessageSource messageSource;

    private static final String EP_STATUS_MESSGE_PREFIX = "patent.eu.details.status.";

    private static final String CUSTOM_FOR_TYPE_20_AND_STATUS_68_KEY = EP_STATUS_MESSGE_PREFIX + "CUSTOM_FOR_TYPE_20_AND_STATUS_68";

    private static final Long DOC_TYPE_CLAIM = 3L;
    private static final Long DOC_TYPE_CLAIM_VERIFIED = 15L;
    private static final Long DOC_TYPE_CLAIM_CHANGED = 27L;
    private static final Long DOC_TYPE_ABSTRACT_LT = 4L;
    private static final Long DOC_TYPE_ABSTRACT_EN = 7L;
    private static final Long DOC_TYPE_APP_SPECIFICATION = 75L;
    private static final Long DOC_TYPE_PATENT_SPECIFICATION = 76L;
    private static final Long DOC_TYPE_PATENT_SPECIFICATION_TEXT = 73L;
    private static final Long DOC_TYPE_SEARCH_REPORT = 122L;
    private static final Long EU_DOC_TYPE_CLAIM = 11L;
    private static final Long EU_DOC_TYPE_CLAIM_VERIFIED = 15L;
    private static final Long EU_DOC_TYPE_CLAIM_CHANGED = 110L;
    private static final List<Long> HIDE_NEXT_PAYMENT_FOR_STATUS_CODE_ID = Arrays.asList(14L, 34L, 36L, 42L, 51L, 60L, 71L, 72L, 74L, 75L, 77L, 78L, 80L, 93L, 112L, 113L, 115L);

    public Dossier convertToDossier(DossierEntity dossierEntity) {
        Dossier dossier;
        if (dossierEntity instanceof LtPatentEntity) {
            dossier = convertToLtPatent((LtPatentEntity) dossierEntity);
        } else if (dossierEntity instanceof EuPatentEntity) {
            dossier = convertToEuPatent((EuPatentEntity) dossierEntity);
        } else if (dossierEntity instanceof SPAEntity) {
            SPA spa = new SPA();
            SPAEntity spaEntity = (SPAEntity) dossierEntity;
            //in case of SPC - we display a certificate number
            if (dossierEntity.getType() == 2L || dossierEntity.getType() == 24L) {
                spa.setNumber(dossierEntity.getNumber());
            }
            spa.setApplicants(convertPersons(spaEntity.getApplicants()));
            spa.setPatentNumber(spaEntity.getParent().getNumber());
            spa.setParentApplicationNumber(spaEntity.getParent().getApplicationNumber());
            spa.setApplicationNumber(spaEntity.getApplicationNumber());
            spa.setApplicationDate(spaEntity.getApplicationDate());
            spa.setF92Number(spaEntity.getF92Number());
            spa.setF92Date(spaEntity.getF92Date());
            spa.setF93Number(spaEntity.getF93Number());
            spa.setF93Date(spaEntity.getF93Date());
            spa.setF93PubDate(spaEntity.getF93PubDate());
            spa.setSpcEffectiveDate(spaEntity.getSpcEffectiveDate());
            spa.setSpcExpiryDate(spaEntity.getSpcExpiryDate());
            spa.setSpcApplicationPubDate(spaEntity.getSpcApplicationPubDate());
            spa.setProtectedProductName(spaEntity.getProtectedProductName());
            spa.setSpcEffectiveDate(spaEntity.getSpcEffectiveDate());
            spa.setExtensionApplicationDate(spaEntity.getExtensionApplicationDate());
            spa.setExtensionExpiryDate(spaEntity.getExtensionExpiryDate());
            spa.setSpcPublicationDate(spaEntity.getSpcPublicationDate());
            if (!isEmpty(dossierEntity.getTitleLt())) {
                spa.setTitleLt(dossierEntity.getTitleLt());
            } else {
                //take info from patent
                spa.setTitleLt(((SPAEntity) dossierEntity).getParent().getTitleLt());

            }
            if (!isEmpty(dossierEntity.getTitleEn())) {
                spa.setTitleEn(dossierEntity.getTitleEn());
            } else {
                spa.setTitleEn(((SPAEntity) dossierEntity).getParent().getTitleEn());
            }

            dossier = spa;

        } else {
            throw new RuntimeException("Document type not recognized: " + dossierEntity.getType());
        }

        dossier.setApplicationPubDate(dossierEntity.getApplicationPubDate());
        dossier.setType(dossierEntity.getType().intValue());

        dossier.setOwners(convertPersons(dossierEntity.getOwners()));
        dossier.setRepresentatives(convertPersons(dossierEntity.getRepresentatives()));

        dossier.setStatusLt(dossierEntity.getStatus().getShowStatus().getDescriptionLt());
        dossier.setStatusEn(dossierEntity.getStatus().getShowStatus().getDescriptionEn());
        dossier.setShowNextFee(!HIDE_NEXT_PAYMENT_FOR_STATUS_CODE_ID.contains(dossierEntity.getStatus().getCode()));

        dossier.setLastFeeDate(dossierEntity.getLastFeeDate());
        dossier.setLastFeeAmount(dossierEntity.getLastFeeAmountCents());
        dossier.setLastFeeCurrency(dossierEntity.getLastFeeCurrency());
        dossier.setLastFeeValidityYears(dossierEntity.getLastFeeValidityYears());
        dossier.setNextFeeDate(dossierEntity.getNextFeeDate());

        dossier.setInvalidationDate(dossierEntity.getInvalidationDate());

        dossier.setUpdatedDate(dossierEntity.getBoLastUpdateDate());

        return dossier;

    }

    public List<DossierListItem> convertSPAsToDossierListItems(Collection<SPAEntity> spaEntities) {
        List<DossierListItem> itemList = new ArrayList<>(spaEntities.size());
        for (SPAEntity entity : spaEntities) {
            SPAListItem item = new SPAListItem();
            item.setTitleLt(entity.getTitleLt() != null ? entity.getTitleLt() : entity.getParent().getTitleLt());
            item.setTitleEn(entity.getTitleEn() != null ? entity.getTitleEn() : entity.getParent().getTitleEn());
            item.setNumber(entity.getNumber());
            item.setPatentNumber(entity.getParent().getNumber());
            item.setApplicationNumber(entity.getApplicationNumber());

            List<Person> personList = new ArrayList<>(entity.getOwners().size());
            for (DossierPersonRoleEntity dossierPersonRole : entity.getOwners()) {
                Person person = new Person();
                person.setFreeformName(dossierPersonRole.getPerson().getName());
                person.setFreeformAddress("");
                person.setCountry(dossierPersonRole.getPerson().getCountryCode());
                personList.add(person);
            }
            item.setOwners(personList);
            item.setStatusLt(entity.getStatus().getShowStatus().getDescriptionLt());
            item.setStatusEn(entity.getStatus().getShowStatus().getDescriptionEn());
            itemList.add(item);
        }
        return itemList;
    }

    private LtPatent convertToLtPatent(LtPatentEntity entity) {
        LtPatent patent = new LtPatent();

        patent.setInventors(convertPersons(entity.getInventors()));
        patent.setNumber(entity.getType() == 1L ? entity.getNumber() : "");
        patent.setTitleLt(entity.getTitleLt());
        patent.setTitleEn(entity.getTitleEn());

        patent.setPatentPubDate(entity.getPatentPubDate());
        patent.setPctNationalLevelStartDate(entity.getPctNatLevelStartDate());
        patent.setApplicationNumber(entity.getApplicationNumber());
        patent.setApplicationDate(entity.getApplicationDate());

        switch (entity.getType().intValue()) {
            case 0:
                patent.setKind("A");
                break;
            case 1:
                patent.setKind("B");
                break;
            case 20:
            case 21:
                patent.setKind("T");
                break;
            default:
        }

        patent.setPriorities(convertPriorities(entity.getPriorities()));
        patent.setHasLicences(CollectionUtils.isNotEmpty(entity.getLicences()));
        patent.setPctData(convertPctData(entity.getInternationalData()));

        List<IPC> ipcList = new ArrayList<>(entity.getClassifications().size());
        for (ClassificationEntity classificationEntity : entity.getClassifications()) {
            IPC ipc = new IPC();
            ipc.setCode(classificationEntity.getIpcCode());
            ipc.setVersion(classificationEntity.getVersion());
            ipcList.add(ipc);
        }
        patent.setIpc(ipcList);
        patent.setApplicants(convertPersons(entity.getApplicants()));

        List<DocumentListItem> documentList = new ArrayList<>(entity.getDocuments().size());
        for (DocumentEntity documentEntity : entity.getDocuments()) {
            DocumentListItem document = new DocumentListItem();
            document.setId(documentEntity.getId());
            document.setType(documentEntity.getType());
            documentList.add(document);
        }
        patent.setDocuments(documentList);

        for (DocumentEntity documentEntity : entity.getDocuments()) {
            DocumentListItem doc = new DocumentListItem();
            doc.setId(documentEntity.getId());
            doc.setType(documentEntity.getType());
            doc.setSize(new BigDecimal(documentEntity.getSize()));

            if (DOC_TYPE_CLAIM.equals(documentEntity.getType())) {
                if (patent.getClaimDoc() == null) {
                    patent.setClaimDoc(doc);
                }
            } else if (EU_DOC_TYPE_CLAIM.equals(documentEntity.getType())
                    && entity.getDocuments().stream().noneMatch(document -> DOC_TYPE_CLAIM.equals(document.getType()))) {
                if (patent.getClaimDoc() == null) {
                    patent.setClaimDoc(doc);
                }
            } else if (DOC_TYPE_CLAIM_VERIFIED.equals(documentEntity.getType())) {
                patent.setClaimDoc(doc);
            } else if (DOC_TYPE_CLAIM_CHANGED.equals(documentEntity.getType())) {
                patent.setClaimDoc(doc);
            } else if (DOC_TYPE_ABSTRACT_EN.equals(documentEntity.getType())) {
                patent.setAbstractEnDoc(doc);
            } else if (DOC_TYPE_ABSTRACT_LT.equals(documentEntity.getType())) {
                patent.setAbstractLtDoc(doc);
            } else if (DOC_TYPE_APP_SPECIFICATION.equals(documentEntity.getType())) {
                patent.setAppSpecificationDoc(doc);
            } else if (DOC_TYPE_PATENT_SPECIFICATION.equals(documentEntity.getType())) {
                patent.setPatentSpecificationDoc(doc);
            } else if (DOC_TYPE_PATENT_SPECIFICATION_TEXT.equals(documentEntity.getType())) {
                patent.setPatentSpecificationTextDoc(doc);
            } else if (DOC_TYPE_SEARCH_REPORT.equals(documentEntity.getType())) {
                patent.setSearchReportDoc(doc);
            }
        }

        patent.setContentLt(entity.getContentLt());

        if(isNotEmpty(entity.getSpas())){
            patent.setSpas(convertSPAsToDossierListItems(entity.getSpas()));
        }

        return patent;
    }

    private List<Person> convertPersons(List<DossierPersonRoleEntity> entityList) {
        List<Person> personList = new ArrayList<>(entityList.size());
        for (DossierPersonRoleEntity dossierPersonRole : entityList) {
            Person person = new Person();
            person.setFreeformName(dossierPersonRole.getPerson().getName());
            person.setFreeformAddress(constructFreeFormAddress(dossierPersonRole.getPerson()));
            person.setCountry(dossierPersonRole.getPerson().getCountryCode());
            person.setSanctionsApplied(dossierPersonRole.getSanctioned());
            personList.add(person);
        }
        return personList;
    }

    private List<Priority> convertPriorities(List<PriorityEntity> entitiesList) {
        List<Priority> priorities = new ArrayList<>(entitiesList.size());
        for (PriorityEntity priorityEntity : entitiesList) {
            Priority priority = new Priority();
            priority.setNumber(priorityEntity.getNumber());
            if (priorityEntity.getDate() != null) {
                priority.setDate(priorityEntity.getDate());
            }
            priority.setCountryCode(priorityEntity.getCountryCode());
            priorities.add(priority);
        }
        return priorities;
    }

    public LicencesList convertToLicencesList(DossierEntity dossierEntity) {
        if (dossierEntity == null) {
            return null;
        }

        List<Licence> licences = new ArrayList<>(dossierEntity.getLicences().size());
        for (LicenceEntity entity : dossierEntity.getLicences()) {
            Licence licence = new Licence();
            licence.setExpiryDate(entity.getLicenceExpiryDate());
            licence.setAgreementRecordDate(entity.getAgreementRecordDate());
            licence.setKind(entity.getLicenceType());
            licence.setLicenciates(convertPersons(entity.getLicenciates()));
            licences.add(licence);
        }

        LicencesList licencesList = new LicencesList();
        licencesList.setLicences(licences);
        licencesList.setApplicationNumber(dossierEntity.getApplicationNumber());
        licencesList.setPatentNumber(dossierEntity.getNumber());
        licencesList.setTitleLt(dossierEntity.getTitleLt());
        licencesList.setTitleEn(dossierEntity.getTitleEn());
        return licencesList;
    }

    private List<InternationalData> convertPctData(List<PctEntity> entitiesList) {
        List<InternationalData> dataList = new ArrayList<>(entitiesList.size());
        for (PctEntity intlDataEntity : entitiesList) {
            InternationalData internationalData = new InternationalData();
            internationalData.setApplicationNumber(intlDataEntity.getApplicationNumber());
            internationalData.setApplicationDate(intlDataEntity.getApplicationDate());
            internationalData.setApplicationPubNumber(intlDataEntity.getApplicationPubNumber());
            internationalData.setApplicationPubDate(intlDataEntity.getApplicationPubDate());
            //used only in EP case (non-PCT)
            internationalData.setIntlPatentIssueDate(intlDataEntity.getIntlPatentIssueDate());
            internationalData.setIntlPatentIssuePubDate(intlDataEntity.getIntlPatentIssuePubDate());
            internationalData.setCountryCode(intlDataEntity.getCountryCode());
            dataList.add(internationalData);
        }
        return dataList;
    }


    private List<Person> convertEuPersons(List<EuPersonEntity> entityList) {
        List<Person> personList = new ArrayList<>(entityList.size());
        for (EuPersonEntity personEntity : entityList) {
            Person person = new Person();
            person.setFreeformName(personEntity.getName());
            person.setFreeformAddress(constructFreeFormAddress(personEntity));
            person.setCountry(personEntity.getCountryCode());
            personList.add(person);
        }
        return personList;
    }

    private List<Priority> convertEuPriorities(List<EuPriorityEntity> entitiesList) {
        List<Priority> priorities = new ArrayList<>(entitiesList.size());
        for (EuPriorityEntity priorityEntity : entitiesList) {
            Priority priority = new Priority();
            priority.setNumber(priorityEntity.getNumber());
            if (priorityEntity.getDate() != null) {
                priority.setDate(priorityEntity.getDate());
            }
            priority.setCountryCode(priorityEntity.getCountryCode());
            priorities.add(priority);
        }
        return priorities;
    }

    private List<InternationalData> convertEuPctData(List<EuPctEntity> entitiesList) {
        List<InternationalData> dataList = new ArrayList<>(entitiesList.size());
        for (EuPctEntity intlDataEntity : entitiesList) {
            InternationalData internationalData = new InternationalData();
            internationalData.setApplicationNumber(intlDataEntity.getApplicationNumber());
            internationalData.setApplicationDate(intlDataEntity.getApplicationDate());
            internationalData.setApplicationPubNumber(intlDataEntity.getApplicationPubNumber());
            internationalData.setApplicationPubDate(intlDataEntity.getApplicationPubDate());
            dataList.add(internationalData);
        }
        return dataList;
    }

    private EuPatent convertToEuPatent(EuPatentEntity entity) {
        EuPatent euPatent = new EuPatent();
        euPatent.setPriorities(convertPriorities(entity.getPriorities()));
        euPatent.setHasLicences(CollectionUtils.isNotEmpty(entity.getLicences()));

        if (isNotEmpty(entity.getPctData())) {
            euPatent.setPctData(convertPctData(entity.getPctData()));
        }
        if (isNotEmpty(entity.getEpData())) {
            euPatent.setEpData(convertPctData(entity.getEpData()).get(0));
        }

        List<IPC> ipcList = new ArrayList<>(entity.getClassifications().size());
        for (ClassificationEntity classificationEntity : entity.getClassifications()) {
            IPC ipc = new IPC();
            ipc.setCode(classificationEntity.getIpcCode());
            if (!StringUtils.isEmpty(classificationEntity.getVersion()))
                ipc.setVersion("(" + classificationEntity.getVersion() + ")");
            ipcList.add(ipc);
        }
        euPatent.setIpc(ipcList);

        euPatent.setNumber(entity.getNumber());
        euPatent.setApplicationNumber(entity.getApplicationNumber());

        euPatent.setTitleLt(entity.getTitleLt());
        euPatent.setTitleEn(entity.getTitleEn());

        euPatent.setInventors(convertPersons(entity.getInventors()));
        euPatent.setOwners(convertPersons(entity.getOwners()));
        euPatent.setRepresentatives(convertPersons(entity.getRepresentatives()));

        euPatent.setEpoStatus(entity.getStatus().getEpoStatus());
        euPatent.setStatusLt(entity.getStatus().getShowStatus().getDescriptionLt());
        euPatent.setStatusEn(entity.getStatus().getShowStatus().getDescriptionEn());

        euPatent.setInvalidationDate(entity.getInvalidationDate());

        euPatent.setUpdatedDate(entity.getBoLastUpdateDate());

        euPatent.setLastFeeDate(entity.getLastFeeDate());
        euPatent.setLastFeeAmount(entity.getLastFeeAmountCents());
        euPatent.setLastFeeCurrency(entity.getLastFeeCurrency());
        euPatent.setLastFeeValidityYears(entity.getLastFeeValidityYears());
        euPatent.setNextFeeDate(entity.getNextFeeDate());

        //we do not show documents for type 20
        if(entity.getType().intValue() != 20) {

            for (DocumentEntity documentEntity : entity.getDocuments()) {
                DocumentListItem doc = new DocumentListItem();
                doc.setId(documentEntity.getId());
                doc.setType(documentEntity.getType());
                doc.setSize(new BigDecimal(documentEntity.getSize()));

                if (EU_DOC_TYPE_CLAIM.equals(documentEntity.getType())) {
                    if (euPatent.getClaimDoc() == null) {
                        euPatent.setClaimDoc(doc);
                    }
                } else if (EU_DOC_TYPE_CLAIM_CHANGED.equals(documentEntity.getType()) && entity.getDocuments().stream().noneMatch(document -> EU_DOC_TYPE_CLAIM.equals(document.getType()))) {
                    if (euPatent.getClaimDoc() == null) {
                        euPatent.setClaimDoc(doc);
                    }
                } else if (EU_DOC_TYPE_CLAIM_VERIFIED.equals(documentEntity.getType())) {
                    euPatent.setClaimDoc(doc);
                    break;
                }
            }
        }

        if(isNotEmpty(entity.getSpas())){
            euPatent.setSpas(convertSPAsToDossierListItems(entity.getSpas()));
        }

        return euPatent;
    }

    public EuDossier convertToEuDossier(EuDossierEntity entity) {
        if (entity == null) {
            return null;
        }

        EuDossier dossier = new EuDossier();
        dossier.setPriorities(convertEuPriorities(entity.getPriorities()));

        if (entity.getEuPatent() != null) {
            dossier.setPatent(convertToEuPatent(entity.getEuPatent()));
        }

        if (isNotEmpty(entity.getPctData())) {
            dossier.setPctData(convertEuPctData(entity.getPctData()));
        }
        if (isNotEmpty(entity.getEpData())) {
            dossier.setEpData(convertEuPctData(entity.getEpData()).get(0));
        }

        List<IPC> ipcList = new ArrayList<>(entity.getClassifications().size());
        for (EuClassificationEntity classificationEntity : entity.getClassifications()) {
            IPC ipc = new IPC();
            ipc.setCode(classificationEntity.getIpcClass());
            if (!StringUtils.isEmpty(classificationEntity.getVersion()))
                ipc.setVersion("(" + classificationEntity.getVersion() + ")");
            ipcList.add(ipc);
        }
        dossier.setIpc(ipcList);

        dossier.setNumber(entity.getPatentNumber());

        dossier.setTitleLt(entity.getTitleLt());
        dossier.setTitleEn(entity.getTitleEn());

        dossier.setInventors(entity.getEuPatent() != null ? convertPersons(entity.getEuPatent().getInventors()) : convertEuPersons(entity.getInventors()));
        dossier.setOwners(entity.getEuPatent() != null ? convertPersons(entity.getEuPatent().getOwners()) : convertEuPersons(entity.getOwners()));
        dossier.setRepresentatives(entity.getEuPatent() != null ? convertPersons(entity.getEuPatent().getRepresentatives()) : convertEuPersons(entity.getRepresentatives()));

        if (entity.getEuPatent() != null) {
            //specific rule for type=20 and statusCode=68
            if(entity.getEuPatent().getType().intValue() == 20
                    && entity.getEuPatent().getStatus().getId().intValue() == 68){
                dossier.setStatusLt(messageSource.getMessage(CUSTOM_FOR_TYPE_20_AND_STATUS_68_KEY, null, parseLocale("lt")));
                dossier.setStatusEn(messageSource.getMessage(CUSTOM_FOR_TYPE_20_AND_STATUS_68_KEY, null, ENGLISH));
                dossier.getPatent().setStatusLt(messageSource.getMessage(CUSTOM_FOR_TYPE_20_AND_STATUS_68_KEY, null, parseLocale("lt")));
                dossier.getPatent().setStatusEn(messageSource.getMessage(CUSTOM_FOR_TYPE_20_AND_STATUS_68_KEY, null, ENGLISH));
            } else {
                dossier.setStatusLt(entity.getEuPatent().getStatus().getShowStatus().getDescriptionLt());
                dossier.setStatusEn(entity.getEuPatent().getStatus().getShowStatus().getDescriptionEn());
            }
            dossier.setStatusCodeId(entity.getEuPatent().getStatus().getCode());
        } else {
            String key = EP_STATUS_MESSGE_PREFIX + entity.getStatusCode();
            dossier.setStatusLt(messageSource.getMessage(key, null, parseLocale("lt")));
            dossier.setStatusEn(messageSource.getMessage(key, null, ENGLISH));
        }

        dossier.setInvalidationDate(entity.getInvalidationDate() != null ? entity.getInvalidationDate() : entity.getEuPatent() != null ? entity.getEuPatent().getInvalidationDate() : null);

        dossier.setUpdatedDate(entity.getBoLastUpdateDate());

        dossier.setApplicationPubDate(entity.getApplicationPubDate());
        dossier.setEuPatentIssuancePublicationDate(entity.getPatentPubDate());
        dossier.setF46Date(entity.getF46Date());

        dossier.setStatusCode(entity.getStatusCode());

        dossier.setShowNextFee(entity.getEuPatent() == null || !HIDE_NEXT_PAYMENT_FOR_STATUS_CODE_ID.contains(entity.getEuPatent().getStatus().getCode()));

        return dossier;
    }

    public DossierListItem convertEuDossierEntityToDossierListItem(EuDossierEntity entity) {

        DossierListItem item = new DossierListItem();
        if(entity.getEuPatent() != null){
            item.setTitleLt(entity.getEuPatent().getTitleLt());
            item.setTitleEn(entity.getEuPatent().getTitleEn());
        } else {
            item.setTitleLt(entity.getTitleLt());
            item.setTitleEn(entity.getTitleEn());
        }
        item.setNumber(entity.getPatentNumber());

        String applicationNumber;
        if (isNotEmpty(entity.getEpData())) {
            applicationNumber = convertEuPctData(entity.getEpData()).get(0).getApplicationNumber();
        } else {
            applicationNumber = entity.getApplicationNumber();
        }
        item.setApplicationNumber(applicationNumber);

        List<Person> personList = entity.getEuPatent() != null
                ? entity.getEuPatent().getOwners().stream().map(Person::from).collect(Collectors.toList())
                : entity.getOwners().stream().map(Person::from).collect(Collectors.toList());
        item.setOwners(personList);
        if (entity.getEuPatent() != null) {
            //specific rule for type=20 and statusCode=68
            if(entity.getEuPatent().getType().intValue() == 20
                    && entity.getEuPatent().getStatus().getId().intValue() == 68){
                item.setStatusLt(messageSource.getMessage(CUSTOM_FOR_TYPE_20_AND_STATUS_68_KEY, null, parseLocale("lt")));
                item.setStatusEn(messageSource.getMessage(CUSTOM_FOR_TYPE_20_AND_STATUS_68_KEY, null, ENGLISH));
            } else {
                item.setStatusLt(entity.getEuPatent().getStatus().getShowStatus().getDescriptionLt());
                item.setStatusEn(entity.getEuPatent().getStatus().getShowStatus().getDescriptionEn());
            }
        } else {
            String key = EP_STATUS_MESSGE_PREFIX + entity.getStatusCode();
            item.setStatusLt(messageSource.getMessage(key, null, parseLocale("lt")));
            item.setStatusEn(messageSource.getMessage(key, null, ENGLISH));
        }
        return item;
    }

}
