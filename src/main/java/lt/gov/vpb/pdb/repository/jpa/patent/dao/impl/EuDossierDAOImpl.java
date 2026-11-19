package lt.gov.vpb.pdb.repository.jpa.patent.dao.impl;

import lombok.extern.slf4j.Slf4j;
import lt.gov.vpb.pdb.form.patent.EuDossierAdvancedSearchForm;
import lt.gov.vpb.pdb.form.patent.EuDossierSearchForm;
import lt.gov.vpb.pdb.model.PaginatedList;
import lt.gov.vpb.pdb.repository.jpa.patent.dao.EuDossierDAO;
import lt.gov.vpb.pdb.repository.jpa.patent.domain.*;
import lt.gov.vpb.pdb.repository.jpa.patent.domain.Views.EuPatentStatusView;
import lt.gov.vpb.pdb.repository.jpa.patent.util.CriteriaBuilderHelper;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static lt.gov.vpb.pdb.model.util.StringMatching.CONTAINS;
import static lt.gov.vpb.pdb.model.util.StringMatching.EQUALS;
import static lt.gov.vpb.pdb.repository.jpa.JPASearchProviderConfig.PDBPATENTS_PERSISTENCE_UNIT;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Repository
@Slf4j
public class EuDossierDAOImpl implements EuDossierDAO {

    @PersistenceContext(unitName = PDBPATENTS_PERSISTENCE_UNIT, name = "pdbpatentsEntityManager")
    protected EntityManager entityManager;

    private static final String EP_VERTIMAS_NEBUVO_PADUOTAS = "EP_VERTIMAS_NEBUVO_PADUOTAS";
    private static final String EP_EFFECTS_OF_THE_PATENT_ARE_LOST_AB_INITIO = "EP_EFFECTS_OF_THE_PATENT_ARE_LOST_AB_INITIO";

    public EuDossierEntity findByApplicationNumber(String applicationNumber) {
        try {
            return entityManager.createQuery("select d from EuDossierEntity d where d.applicationNumber = :applicationNumber", EuDossierEntity.class)
                    .setParameter("applicationNumber", applicationNumber)
                    .getSingleResult();
        }catch (NoResultException e){
            return null;
        }
    }

    public EuDossierEntity findByPatentNumber(String patentNumber) {
        try {
            return entityManager.createQuery("select d from EuDossierEntity d where d.patentNumber = :patentNumber", EuDossierEntity.class)
                    .setParameter("patentNumber", patentNumber)
                    .getSingleResult();
        }catch (NoResultException e){
            return null;
        }
    }

    public List<EuPatentStatusView> getPatentEuStatuses() {
        try {
            return entityManager
                    .createQuery("SELECT s FROM EuPatentStatusView s", EuPatentStatusView.class)
                    .getResultList();
        } catch(NoResultException e){
            return null;
        }
    }

    @Override
    public PaginatedList<EuDossierEntity> search(EuDossierSearchForm searchForm) {

        CriteriaBuilderHelper<EuDossierEntity> criteriaBuilderHelper = new CriteriaBuilderHelper<>(entityManager, EuDossierEntity.class);

        criteriaBuilderHelper.appendPredicate(EuDossierEntity_.patentNumber, searchForm.getF11_mc(), searchForm.getF11());

        if (isNotBlank(searchForm.getStatus())) {
            if (EP_VERTIMAS_NEBUVO_PADUOTAS.equals(searchForm.getStatus()) || EP_EFFECTS_OF_THE_PATENT_ARE_LOST_AB_INITIO.equals(searchForm.getStatus())) {
                criteriaBuilderHelper.appendPredicate(EuDossierEntity_.statusCode, EQUALS, searchForm.getStatus());
            } else {
                criteriaBuilderHelper.<EuDossierEntity, EuPatentEntity>appendJoin(EuDossierEntity_.EU_PATENT);
                criteriaBuilderHelper.<StatusEntity, EuPatentEntity>appendJoin(EuPatentEntity_.STATUS, EuDossierEntity_.EU_PATENT);
                String[] statusValues = Optional.ofNullable(searchForm.getStatus())
                        .map(s -> s.split(","))
                        .orElse(new String[0]);
                criteriaBuilderHelper.appendJoinInPredicate(EuPatentEntity_.STATUS,
                        StatusEntity_.id,
                        (Object[]) statusValues
                );
            }
        }

        if (searchForm instanceof EuDossierAdvancedSearchForm) {
            processAdvancedForm((EuDossierAdvancedSearchForm) searchForm, criteriaBuilderHelper);
        }
        criteriaBuilderHelper.setOrder("desc", EuDossierEntity_.APPLICATION_DATE);

        List<EuDossierEntity> list = entityManager.createQuery(criteriaBuilderHelper.getQuery())
                .setFirstResult(searchForm.getOffset())
                .setMaxResults(searchForm.getSize())
                .getResultList();

        Number count = entityManager.createQuery(criteriaBuilderHelper.getCountQuery()).getSingleResult();
        return new PaginatedList<>(list, count.longValue(), searchForm.getOffset(), list.size());
    }

    private void processAdvancedForm(EuDossierAdvancedSearchForm searchForm, CriteriaBuilderHelper criteriaBuilderHelper) {
        EuDossierAdvancedSearchForm advancedSearchForm = searchForm;

        if (isNotBlank(advancedSearchForm.getF74_name()) || isNotBlank(advancedSearchForm.getF74_address())) {
            criteriaBuilderHelper.<EuDossierEntity, EuPatentEntity>appendJoin(EuDossierEntity_.EU_PATENT);
            criteriaBuilderHelper.<EuPatentEntity, DossierPersonRoleEntity>appendJoin(EuPatentEntity_.REPRESENTATIVES, EuDossierEntity_.EU_PATENT);
        }

        criteriaBuilderHelper.<EuClassificationEntity, String>appendJoinPredicate(EuDossierEntity_.CLASSIFICATIONS,
                EuClassificationEntity_.ipcClass,
                advancedSearchForm.getF51_mc(),
                advancedSearchForm.getF51()
        );

        /*
            Owner criteria
         */
        criteriaBuilderHelper.<EuPersonEntity, String>appendJoinPredicate(EuDossierEntity_.OWNERS,
                EuPersonEntity_.name,
                advancedSearchForm.getF73_name_mc(),
                advancedSearchForm.getF73_name()
        );

        criteriaBuilderHelper.<EuPersonEntity, String>appendJoinPredicate(EuDossierEntity_.OWNERS,
                EuPersonEntity_.address,
                CONTAINS,
                advancedSearchForm.getF73_address()
        );

        criteriaBuilderHelper.<EuPersonEntity, String>appendJoinPredicate(EuDossierEntity_.OWNERS,
                EuPersonEntity_.countryCode,
                EQUALS,
                advancedSearchForm.getF73_country()
        );

        /*
            Representative criteria
         */
        criteriaBuilderHelper.<PersonEntity, String>appendJoinPredicate(DossierPersonRoleEntity_.PERSON,
                PersonEntity_.name,
                advancedSearchForm.getF74_name_mc(),
                advancedSearchForm.getF74_name(),
                EuPatentEntity_.REPRESENTATIVES
        );

        criteriaBuilderHelper.<PersonEntity, String>appendJoinPredicate(DossierPersonRoleEntity_.PERSON,
                PersonEntity_.address,
                CONTAINS,
                advancedSearchForm.getF74_address(),
                EuPatentEntity_.REPRESENTATIVES
        );

        /*
            Inventor criteria
         */
        criteriaBuilderHelper.<EuPersonEntity, String>appendJoinPredicate(EuDossierEntity_.INVENTORS,
                EuPersonEntity_.name,
                advancedSearchForm.getF72_name_mc(),
                advancedSearchForm.getF72_name()
        );

        criteriaBuilderHelper.<EuPersonEntity, String>appendJoinPredicate(EuDossierEntity_.INVENTORS,
                EuPersonEntity_.countryCode,
                EQUALS,
                advancedSearchForm.getF72_country()
        );

        /*
            Words in Title / Abstract
         */
        criteriaBuilderHelper.appendPredicate(EuDossierEntity_.titleLt, advancedSearchForm.getF54_mc(), advancedSearchForm.getF54());

        /*
            Priorities criteria
         */
        criteriaBuilderHelper.appendJoinPredicate(EuDossierEntity_.PRIORITIES,
                EuPriorityEntity_.number,
                advancedSearchForm.getF30_number_mc(),
                advancedSearchForm.getF30_number()
        );
        criteriaBuilderHelper.<EuPriorityEntity, LocalDate>appendJoinPredicate(EuDossierEntity_.PRIORITIES,
                EuPriorityEntity_.date,
                EQUALS,
                advancedSearchForm.getF30_date()
        );

        /*
           (86) PCT (international data) application criteria
         */
        criteriaBuilderHelper.<EuPctEntity, String>appendJoinPredicate(EuDossierEntity_.PCT_DATA,
                EuPctEntity_.applicationNumber,
                advancedSearchForm.getF86_appnumber_mc(),
                advancedSearchForm.getF86_appnumber()
        );
        criteriaBuilderHelper.<EuPctEntity, LocalDate>appendJoinPredicate(EuDossierEntity_.PCT_DATA,
                EuPctEntity_.applicationDate,
                EQUALS,
                advancedSearchForm.getF86_appdate()
        );

        /*
            (87) PCT (international data) publication criteria
         */
        criteriaBuilderHelper.<EuPctEntity, String>appendJoinPredicate(EuDossierEntity_.PCT_DATA,
                EuPctEntity_.applicationPubNumber,
                advancedSearchForm.getF87_pubnumber_mc(),
                advancedSearchForm.getF87_pubnumber()
        );
        criteriaBuilderHelper.<EuPctEntity, LocalDate>appendJoinPredicate(EuDossierEntity_.PCT_DATA,
                EuPctEntity_.applicationPubDate,
                EQUALS,
                advancedSearchForm.getF87_pubdate()
        );

        /*
            (96) EU application criteria
         */
        criteriaBuilderHelper.<EuPctEntity, String>appendJoinPredicate(EuDossierEntity_.EP_DATA,
                EuPctEntity_.applicationNumber,
                advancedSearchForm.getF96_appnumber_mc(),
                advancedSearchForm.getF96_appnumber()
        );
        criteriaBuilderHelper.<EuPctEntity, LocalDate>appendJoinPredicate(EuDossierEntity_.EP_DATA,
                EuPctEntity_.applicationDate,
                EQUALS,
                advancedSearchForm.getF96_appdate()
        );


        /*
            (45) European patent issuing publication date
         */
        criteriaBuilderHelper.appendPredicate(EuDossierEntity_.patentPubDate, EQUALS, advancedSearchForm.getF45());

        /*
            (46) Date of publication of the claims translation
         */
         criteriaBuilderHelper.appendPredicate(EuDossierEntity_.f46Date, EQUALS, advancedSearchForm.getF46());


    }


}

