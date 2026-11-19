package lt.gov.vpb.pdb.repository.jpa.patent.dao;

import lt.gov.vpb.pdb.form.patent.DossierSearchForm;
import lt.gov.vpb.pdb.form.patent.EuDossierAdvancedSearchForm;
import lt.gov.vpb.pdb.form.patent.LtDossierAdvancedSearchForm;
import lt.gov.vpb.pdb.model.PaginatedList;
import lt.gov.vpb.pdb.repository.jpa.patent.domain.DossierEntity;
import lt.gov.vpb.pdb.repository.jpa.patent.domain.SPAEntity;
import lt.gov.vpb.pdb.repository.jpa.patent.domain.Views.PatentStatusView;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface LtDossierDAO //extends JpaRepository<DossierEntity, Long>
{

    /**
     * Find DossierEntity by number
     *
     * @param number
     * @return
     */
    DossierEntity findByNumber(String number);

    /**
     * Find DossierEntity by application number
     *
     * @param applicationNumber
     * @return
     */
    DossierEntity findByApplicationNumber(String applicationNumber);

    /**
     * Search DossierEntity(ies)
     * @param searchForm
     * @return
     */
    PaginatedList<? extends DossierEntity> search(DossierSearchForm searchForm);

    /**
     * Search European patent SPAs. Eu and Lt SPAs are stored in the same table
     * @param searchForm
     * @return
     */
    PaginatedList<SPAEntity> searchEuSPA(EuDossierAdvancedSearchForm searchForm);

    /**
     * Search Lt SPAs. Eu and Lt SPAs are stored in the same table
     * @param searchForm
     * @return
     */
    PaginatedList<SPAEntity> searchLtSPA(LtDossierAdvancedSearchForm searchForm);
    List<PatentStatusView> getPatentStatuses();

}
