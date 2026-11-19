package lt.gov.vpb.pdb.repository.jpa.patent.dao.impl;


import lombok.extern.slf4j.Slf4j;
import lt.gov.vpb.pdb.form.patent.*;
import lt.gov.vpb.pdb.model.PaginatedList;
import lt.gov.vpb.pdb.repository.jpa.patent.dao.LtDossierDAO;
import lt.gov.vpb.pdb.repository.jpa.patent.domain.*;
import lt.gov.vpb.pdb.repository.jpa.patent.domain.Views.PatentStatusView;
import lt.gov.vpb.pdb.repository.jpa.patent.util.CriteriaBuilderHelper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static lt.gov.vpb.pdb.model.util.StringMatching.CONTAINS;
import static lt.gov.vpb.pdb.model.util.StringMatching.EQUALS;
import static lt.gov.vpb.pdb.repository.jpa.JPASearchProviderConfig.PDBPATENTS_PERSISTENCE_UNIT;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Repository
@Slf4j
public class LtDossierDAOImpl implements LtDossierDAO {

    @PersistenceContext(unitName = PDBPATENTS_PERSISTENCE_UNIT, name = "pdbpatentsEntityManager")
    protected EntityManager entityManager;

    public DossierEntity findByNumber(String number) {
        try {
            return entityManager.createQuery("select p from DossierEntity p where p.number= :number order by p.boLastUpdateDate desc", DossierEntity.class)
                    .setParameter("number", number)
                    .setFirstResult(0)
                    .setMaxResults(1)
                    .getResultList()
                    .get(0);
        } catch(NoResultException e){
            return null;
        }
    }

    public DossierEntity findByApplicationNumber(String applicationNumber) {
        try {
            return entityManager.createQuery("select p from DossierEntity p where p.applicationNumber= :applicationNumber", DossierEntity.class)
                    .setParameter("applicationNumber", applicationNumber)
                    .getSingleResult();
        } catch(NoResultException e){
            return null;
        }
    }

    public List<PatentStatusView> getPatentStatuses() {
        try {
            return entityManager
                    .createQuery("SELECT s FROM PatentStatusView s", PatentStatusView.class)
                    .getResultList();
        } catch(NoResultException e){
            return null;
        }
    }

    @Override
    public PaginatedList<? extends DossierEntity> search(DossierSearchForm searchForm) {

        //If SPA is provided, ignore everything else and search only by SPA number
        if(searchForm instanceof LtDossierAdvancedSearchForm
                && (!isEmpty(((LtDossierAdvancedSearchForm)searchForm).getSpa()) || !isEmpty(((LtDossierAdvancedSearchForm)searchForm).getSpa_f95()))){
            return searchLtSPA((LtDossierAdvancedSearchForm)searchForm);
        } else {
            CriteriaBuilderHelper<LtPatentEntity> criteriaBuilderHelper = new CriteriaBuilderHelper<>(entityManager, LtPatentEntity.class);
            criteriaBuilderHelper.appendPredicate(DossierEntity_.number, searchForm.getF11_mc(), searchForm.getF11());
            if (!StringUtils.isEmpty(searchForm.getStatus())) {
                String[] statusValues = Optional.ofNullable(searchForm.getStatus())
                        .map(s -> s.split(","))
                        .orElse(new String[0]);
                criteriaBuilderHelper.appendInPredicate(DossierEntity_.status, (Object[]) statusValues);
            }
            if (searchForm instanceof LtDossierAdvancedSearchForm) {
                processAdvancedForm((LtDossierAdvancedSearchForm) searchForm, criteriaBuilderHelper);
            } else if (searchForm instanceof LtDossierTextSearchForm) {
                LtDossierTextSearchForm textSearchForm = ((LtDossierTextSearchForm) searchForm);
                criteriaBuilderHelper.appendStringTokensPredicate(LtPatentEntity_.contentLt, textSearchForm.getF57_mc(), textSearchForm.getF57());
            } else if (searchForm instanceof LtDossierSearchForm) {
                LtDossierSearchForm ltDossierSearchForm = (LtDossierSearchForm) searchForm;
                criteriaBuilderHelper.appendPredicate(DossierEntity_.applicationNumber, ltDossierSearchForm.getF21_mc(), ltDossierSearchForm.getF21());
            }
            criteriaBuilderHelper.setOrder("desc", DossierEntity_.APPLICATION_DATE);
            criteriaBuilderHelper.getQuery().distinct(true);

            List<LtPatentEntity> list = entityManager.createQuery(criteriaBuilderHelper.getQuery())
                    .setFirstResult(searchForm.getOffset())
                    .setMaxResults(searchForm.getSize())
                    .getResultList();

            Number count = entityManager.createQuery(criteriaBuilderHelper.getCountQuery()).getSingleResult();
            return new PaginatedList<>(list, count.longValue(), searchForm.getOffset(), list.size());
        }
    }

    @Override
    public PaginatedList<SPAEntity> searchEuSPA(EuDossierAdvancedSearchForm searchForm) {
        return searchSPAs(searchForm, new Long[]{23L, 24L}, searchForm.getOffset(), searchForm.getSize());
    }

    @Override
    public PaginatedList<SPAEntity> searchLtSPA(LtDossierAdvancedSearchForm searchForm) {
        return searchSPAs(searchForm, new Long[]{2L, 11L}, searchForm.getOffset(), searchForm.getSize());
    }

    private PaginatedList<SPAEntity> searchSPAs(EuDossierAdvancedSearchForm searchForm, Long[] dossierTypeIds, int offset, int size){
        CriteriaBuilderHelper<SPAEntity> criteriaBuilderHelper = new CriteriaBuilderHelper<>(entityManager, SPAEntity.class);
        if (!StringUtils.isEmpty(searchForm.getSpa())) {
            criteriaBuilderHelper.appendPredicate(SPAEntity_.applicationNumber, searchForm.getSpa_mc(), searchForm.getSpa());
        }
        if (!StringUtils.isEmpty(searchForm.getSpa_f95())) {
            criteriaBuilderHelper.appendPredicate(SPAEntity_.protectedProductName, searchForm.getSpa_f95_mc(), searchForm.getSpa_f95());
        }
        criteriaBuilderHelper.appendInPredicate(SPAEntity_.type, dossierTypeIds);
        criteriaBuilderHelper.setOrder("desc", SPAEntity_.APPLICATION_DATE);
        List<SPAEntity> list = entityManager.createQuery(criteriaBuilderHelper.getQuery())
                .setFirstResult(offset)
                .setMaxResults(size)
                .getResultList();

        Number count = entityManager.createQuery(criteriaBuilderHelper.getCountQuery()).getSingleResult();
        return new PaginatedList<>(list, count.longValue(), offset, list.size());
    }

    private PaginatedList<SPAEntity> searchSPAs(LtDossierAdvancedSearchForm searchForm, Long[] dossierTypeIds, int offset, int size){
        CriteriaBuilderHelper<SPAEntity> criteriaBuilderHelper = new CriteriaBuilderHelper<>(entityManager, SPAEntity.class);
        if (!StringUtils.isEmpty(searchForm.getSpa())) {
            criteriaBuilderHelper.appendPredicate(SPAEntity_.applicationNumber, searchForm.getSpa_mc(), searchForm.getSpa());
        }
        if (!StringUtils.isEmpty(searchForm.getSpa_f95())) {
            criteriaBuilderHelper.appendPredicate(SPAEntity_.protectedProductName, searchForm.getSpa_f95_mc(), searchForm.getSpa_f95());
        }
        criteriaBuilderHelper.appendInPredicate(DossierEntity_.type, dossierTypeIds);
        criteriaBuilderHelper.setOrder("desc", DossierEntity_.APPLICATION_DATE);
        List<SPAEntity> list = entityManager.createQuery(criteriaBuilderHelper.getQuery())
                .setFirstResult(offset)
                .setMaxResults(size)
                .getResultList();

        Number count = entityManager.createQuery(criteriaBuilderHelper.getCountQuery()).getSingleResult();
        return new PaginatedList<>(list, count.longValue(), offset, list.size());
    }

    private void processAdvancedForm(LtDossierSearchForm searchForm, CriteriaBuilderHelper criteriaBuilderHelper) {
        LtDossierAdvancedSearchForm advancedSearchForm = (LtDossierAdvancedSearchForm) searchForm;

        if (isNotBlank(advancedSearchForm.getF71_name()) || isNotBlank(advancedSearchForm.getF71_address()) || isNotBlank(advancedSearchForm.getF71_country())) criteriaBuilderHelper.<DossierEntity, DossierPersonRoleEntity>appendJoin(DossierEntity_.APPLICANTS);
        if (isNotBlank(advancedSearchForm.getF73_name()) || isNotBlank(advancedSearchForm.getF73_address()) || isNotBlank(advancedSearchForm.getF73_country())) criteriaBuilderHelper.<DossierEntity, DossierPersonRoleEntity>appendJoin(DossierEntity_.OWNERS);
        if (isNotBlank(advancedSearchForm.getF74_name()) || isNotBlank(advancedSearchForm.getF74_address())) criteriaBuilderHelper.<DossierEntity, DossierPersonRoleEntity>appendJoin(DossierEntity_.REPRESENTATIVES);
        if (isNotBlank(advancedSearchForm.getF72_name()) || isNotBlank(advancedSearchForm.getF72_country())) criteriaBuilderHelper.<DossierEntity, DossierPersonRoleEntity>appendJoin(DossierEntity_.INVENTORS);

        criteriaBuilderHelper.appendPredicate(DossierEntity_.applicationNumber, advancedSearchForm.getF21_mc(), advancedSearchForm.getF21());

        criteriaBuilderHelper.<ClassificationEntity, String>appendJoinPredicate(DossierEntity_.CLASSIFICATIONS,
                ClassificationEntity_.ipcCode,
                advancedSearchForm.getF51_mc(),
                advancedSearchForm.getF51()
        );

        /*
            Applicant criteria
         */
        criteriaBuilderHelper.<PersonEntity, String>appendJoinPredicate(DossierPersonRoleEntity_.PERSON,
                PersonEntity_.name,
                advancedSearchForm.getF71_name_mc(),
                advancedSearchForm.getF71_name(),
                DossierEntity_.APPLICANTS
        );

        criteriaBuilderHelper.<PersonEntity, String>appendJoinPredicate(DossierPersonRoleEntity_.PERSON,
                PersonEntity_.address,
                CONTAINS,
                advancedSearchForm.getF71_address(),
                DossierEntity_.APPLICANTS
        );

        criteriaBuilderHelper.<PersonEntity, String>appendJoinPredicate(DossierPersonRoleEntity_.PERSON,
                PersonEntity_.countryCode,
                EQUALS,
                advancedSearchForm.getF71_country(),
                DossierEntity_.APPLICANTS
        );

        /*
            Owner criteria
         */
        criteriaBuilderHelper.<PersonEntity, String>appendJoinPredicate(DossierPersonRoleEntity_.PERSON,
                PersonEntity_.name,
                advancedSearchForm.getF73_name_mc(),
                advancedSearchForm.getF73_name(),
                DossierEntity_.OWNERS
        );

        criteriaBuilderHelper.<PersonEntity, String>appendJoinPredicate(DossierPersonRoleEntity_.PERSON,
                PersonEntity_.address,
                CONTAINS,
                advancedSearchForm.getF73_address(),
                DossierEntity_.OWNERS
        );

        criteriaBuilderHelper.<PersonEntity, String>appendJoinPredicate(DossierPersonRoleEntity_.PERSON,
                PersonEntity_.countryCode,
                EQUALS,
                advancedSearchForm.getF73_country(),
                DossierEntity_.OWNERS
        );

        /*
            Representative criteria
         */
        criteriaBuilderHelper.<PersonEntity, String>appendJoinPredicate(DossierPersonRoleEntity_.PERSON,
                PersonEntity_.name,
                advancedSearchForm.getF74_name_mc(),
                advancedSearchForm.getF74_name(),
                DossierEntity_.REPRESENTATIVES
        );

        criteriaBuilderHelper.<PersonEntity, String>appendJoinPredicate(DossierPersonRoleEntity_.PERSON,
                PersonEntity_.address,
                CONTAINS,
                advancedSearchForm.getF74_address(),
                DossierEntity_.REPRESENTATIVES
        );

        /*
            Inventor criteria
         */
        criteriaBuilderHelper.<PersonEntity, String>appendJoinPredicate(DossierPersonRoleEntity_.PERSON,
                PersonEntity_.name,
                advancedSearchForm.getF72_name_mc(),
                advancedSearchForm.getF72_name(),
                DossierEntity_.INVENTORS
        );

        criteriaBuilderHelper.<PersonEntity, String>appendJoinPredicate(DossierPersonRoleEntity_.PERSON,
                PersonEntity_.countryCode,
                EQUALS,
                advancedSearchForm.getF72_country(),
                DossierEntity_.INVENTORS
        );

        /*
            Words in Title / Abstract
         */
        criteriaBuilderHelper.appendPredicate(DossierEntity_.titleLt, advancedSearchForm.getF54_mc(), advancedSearchForm.getF54());
        criteriaBuilderHelper.appendPredicate(DossierEntity_.descriptionLt, advancedSearchForm.getF57_mc(), advancedSearchForm.getF57());

        /*
            Priorities criteria
         */
        criteriaBuilderHelper.appendJoinPredicate(DossierEntity_.PRIORITIES,
                PriorityEntity_.number,
                advancedSearchForm.getF30_number_mc(),
                advancedSearchForm.getF30_number()
        );
        criteriaBuilderHelper.<PriorityEntity, LocalDate>appendJoinPredicate(DossierEntity_.PRIORITIES,
                PriorityEntity_.date,
                EQUALS,
                advancedSearchForm.getF30_date()
        );

        /*
            PCT (international data) application criteria
         */
        criteriaBuilderHelper.appendJoinPredicate(LtPatentEntity_.INTERNATIONAL_DATA,
                PctEntity_.applicationNumber,
                advancedSearchForm.getF86_appnumber_mc(),
                advancedSearchForm.getF86_appnumber()
        );
        criteriaBuilderHelper.appendJoinPredicate(LtPatentEntity_.INTERNATIONAL_DATA,
                PctEntity_.applicationDate,
                EQUALS,
                advancedSearchForm.getF86_appdate()
        );

        /*
            PCT (international data) publication criteria
         */
        criteriaBuilderHelper.appendJoinPredicate(LtPatentEntity_.INTERNATIONAL_DATA,
                PctEntity_.applicationPubNumber,
                advancedSearchForm.getF87_pubnumber_mc(),
                advancedSearchForm.getF87_pubnumber()
        );
        criteriaBuilderHelper.appendJoinPredicate(LtPatentEntity_.INTERNATIONAL_DATA,
                PctEntity_.applicationPubDate,
                EQUALS,
                advancedSearchForm.getF87_pubdate()
        );

    }


}

