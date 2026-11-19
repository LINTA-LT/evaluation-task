package lt.gov.vpb.pdb.repository.jpa.patent;

import lt.gov.vpb.pdb.form.patent.EuDossierAdvancedSearchForm;
import lt.gov.vpb.pdb.form.patent.EuDossierSearchForm;
import lt.gov.vpb.pdb.form.patent.LtDossierSearchForm;
import lt.gov.vpb.pdb.form.patent.LtDossierTextSearchForm;
import lt.gov.vpb.pdb.model.PaginatedList;
import lt.gov.vpb.pdb.model.Person;
import lt.gov.vpb.pdb.model.patent.*;
import lt.gov.vpb.pdb.repository.jpa.patent.dao.EuDossierDAO;
import lt.gov.vpb.pdb.repository.jpa.patent.dao.LtDossierDAO;
import lt.gov.vpb.pdb.repository.jpa.patent.domain.*;
import lt.gov.vpb.pdb.repository.jpa.patent.util.DossierConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static lt.gov.vpb.pdb.model.util.StringMatching.CONTAINS;
import static org.apache.commons.lang.StringUtils.isBlank;

@Service
@Transactional(transactionManager = "pdbpatentsTransactionManager", readOnly = true)
public class DossierJpaRepository {

    @Autowired
    LtDossierDAO ltDossierDAO;

    @Autowired
    EuDossierDAO euDossierDAO;

    @Autowired
    DossierConverter dossierConverter;


    public PaginatedList<DossierListItem> euDossierSearch(EuDossierSearchForm searchForm) {
        List<DossierListItem> itemList = new ArrayList<>();
        if (searchForm instanceof EuDossierAdvancedSearchForm
                && (!StringUtils.isEmpty(((EuDossierAdvancedSearchForm) searchForm).getSpa())
                || !StringUtils.isEmpty(((EuDossierAdvancedSearchForm) searchForm).getSpa_f95()))) {
            PaginatedList<SPAEntity> paginatedList = ltDossierDAO.searchEuSPA((EuDossierAdvancedSearchForm) searchForm);
            itemList = dossierConverter.convertSPAsToDossierListItems(paginatedList.getData());
            return new PaginatedList<>(itemList, paginatedList.getTotalItems(), searchForm.getOffset(), itemList.size());
        } else {
            PaginatedList<EuDossierEntity> paginatedEntityList = euDossierDAO.search(searchForm);
            for (EuDossierEntity entity : paginatedEntityList.getData()) {
                itemList.add(dossierConverter.convertEuDossierEntityToDossierListItem(entity));
            }
            return new PaginatedList<>(itemList, paginatedEntityList.getTotalItems(), searchForm.getOffset(), itemList.size());
        }
    }

    public Dossier findLtDossierDetails(String number) {
        DossierEntity entity = ltDossierDAO.findByApplicationNumber(number);
        if (entity != null) {
            return dossierConverter.convertToDossier(entity);
        } else {
            return null;
        }
    }

    public LicencesList findDossierLicences(String number) {
        return dossierConverter.convertToLicencesList(ltDossierDAO.findByNumber(number));
    }

    public EuDossier findEuDossierDetailsByApplicationNumber(String number) {
        return (dossierConverter.convertToEuDossier(euDossierDAO.findByApplicationNumber(number)));
    }

    public EuDossier findEuDossierDetailsByPatentNumber(String number) {
        return (dossierConverter.convertToEuDossier(euDossierDAO.findByPatentNumber(number)));
    }

    public PaginatedList<DossierListItem> ltDossierSearch(LtDossierSearchForm searchForm) {
        PaginatedList<? extends DossierEntity> paginatedEntityList = ltDossierDAO.search(searchForm);
        List<DossierListItem> itemList = new ArrayList<>();
        for (DossierEntity dossierEntity : paginatedEntityList.getData()) {
            DossierListItem item;
            if (dossierEntity instanceof SPAEntity) {
                item = new SPAListItem();
                ((SPAListItem) item).setPatentNumber((((SPAEntity) dossierEntity).getParent().getNumber()));
            } else {
                item = new DossierListItem();
            }
            item.setId(dossierEntity.getId());
            item.setNumber(dossierEntity.getNumber());
            item.setType(dossierEntity.getType().intValue());
            item.setApplicationNumber(dossierEntity.getApplicationNumber());
            item.setTitleLt(dossierEntity.getTitleLt());
            item.setTitleEn(dossierEntity.getTitleEn());
            List<Person> personList = new ArrayList<>(dossierEntity.getApplicants().size());
            for (DossierPersonRoleEntity dossierPersonRole : dossierEntity.getApplicants()) {
                Person person = new Person();
                person.setFreeformName(dossierPersonRole.getPerson().getName());
                person.setFreeformAddress("");
                person.setCountry(dossierPersonRole.getPerson().getCountryCode());
                personList.add(person);
            }
            item.setApplicants(personList);
            item.setStatusLt(dossierEntity.getStatus().getShowStatus().getDescriptionLt());
            item.setStatusEn(dossierEntity.getStatus().getShowStatus().getDescriptionEn());
            if (searchForm instanceof LtDossierTextSearchForm) {
                item.setContentLt(prepareHighlightedContent(
                        ((LtPatentEntity) dossierEntity).getContentLt(),
                        (LtDossierTextSearchForm) searchForm));
            }
            itemList.add(item);
        }
        return new PaginatedList<>(itemList, paginatedEntityList.getTotalItems(), searchForm.getOffset(), itemList.size());
    }

    private String prepareHighlightedContent(String content, LtDossierTextSearchForm textSearchForm) {
        if (isBlank(content) || isBlank(textSearchForm.getF57())) {
            return null;
        }
        StringBuffer result = new StringBuffer(content);
        String contentLc = content.toLowerCase();
        String highlight = "<span style=\"background: #F0E68C;\">";
        String highlightText;

        if (textSearchForm.getF57_mc() == CONTAINS) {
            highlightText = textSearchForm.getF57();
        } else { //CONTAINS_ALL or CONTAINS_ANY, we will highlight the first available word
            highlightText = new StringTokenizer(textSearchForm.getF57(), " ").nextToken();

        }
        int start = contentLc.indexOf(highlightText.toLowerCase());
        if (start < 0) {
            // not found, nothing to to
            return content;
        } else {
            int spanStart = Math.min(start, content.length());
            int spanEnd = Math.min(spanStart + highlight.length() + highlightText.length(), content.length());
            result.insert(spanStart, highlight);
            result.insert(spanEnd, "</span>");

            int sentenceStart = Math.max(0, result.substring(0, spanStart).lastIndexOf('.'));
            int sentenceEnd = Math.min(result.length(), spanEnd + 100);

            return result.substring(sentenceStart + (start == 0 ? 0 : 1), sentenceEnd);
        }

    }

}
