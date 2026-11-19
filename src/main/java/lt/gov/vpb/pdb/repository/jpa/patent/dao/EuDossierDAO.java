package lt.gov.vpb.pdb.repository.jpa.patent.dao;


import lt.gov.vpb.pdb.form.patent.EuDossierSearchForm;
import lt.gov.vpb.pdb.model.PaginatedList;
import lt.gov.vpb.pdb.repository.jpa.patent.domain.EuDossierEntity;
import lt.gov.vpb.pdb.repository.jpa.patent.domain.Views.EuPatentStatusView;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface EuDossierDAO //extends JpaRepository<EuDossierEntity, Long>
 {

    /**
     * Find EuDossierEntity by application number
     * @param number
     * @return
     */
    EuDossierEntity findByApplicationNumber(String number);

     /**
      * Find EuDossierEntity by patent number
      * @param number
      * @return
      */
     EuDossierEntity findByPatentNumber(String number);

    /**
     * Search EuDossierEntity(ies)
     * @param searchForm
     * @return
     */
    PaginatedList<EuDossierEntity> search(EuDossierSearchForm searchForm);
    List<EuPatentStatusView> getPatentEuStatuses();

}

