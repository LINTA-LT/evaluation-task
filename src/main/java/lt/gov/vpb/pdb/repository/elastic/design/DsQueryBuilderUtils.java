package lt.gov.vpb.pdb.repository.elastic.design;

import lombok.extern.slf4j.Slf4j;
import lt.gov.vpb.pdb.form.DsAdvancedSearchForm;
import lt.gov.vpb.pdb.form.DsSearchForm;
import lt.gov.vpb.pdb.repository.elastic.util.EleasticQueryBuilderUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import static lt.gov.vpb.pdb.model.util.StringMatching.CONTAINS;
import static lt.gov.vpb.pdb.repository.elastic.util.FieldDefinitions.DesignFields.*;
import static lt.gov.vpb.pdb.repository.elastic.util.FieldDefinitions.PersonFields.FREEFORM_ADDRESS;
import static lt.gov.vpb.pdb.repository.elastic.util.FieldDefinitions.PersonFields.FREEFORM_NAME;
import static org.springframework.util.StringUtils.isEmpty;

@Slf4j
public class DsQueryBuilderUtils extends EleasticQueryBuilderUtils {

    public static QueryBuilder buildQuery(DsSearchForm searchForm) {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (!isEmpty(searchForm.getF54())) {
            boolQueryBuilder.must(buildStringKeywordQuery(searchForm.getF54_mc(), DESCRIPTION, searchForm.getF54()));
        }
        if (!isEmpty(searchForm.getF21())) {
            boolQueryBuilder.must(buildStringKeywordQuery(searchForm.getF21_mc(), APPLICATION_NUMBER, searchForm.getF21()));
        }
        if (!isEmpty(searchForm.getF11())) {
            boolQueryBuilder.must(buildStringKeywordQuery(searchForm.getF11_mc(), REGISTRATION_NUMBER, searchForm.getF11()));
        }

        if (!isEmpty(searchForm.getF22_from())) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(APPLICATION_DATE).timeZone(TIMEZONE_OFFSET).from(searchForm.getF22_from()));
        }
        if (!isEmpty(searchForm.getF22_to())) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(APPLICATION_DATE).timeZone(TIMEZONE_OFFSET).to(searchForm.getF22_to()));
        }

        if (searchForm instanceof DsAdvancedSearchForm) {

            DsAdvancedSearchForm advancedSearchForm = (DsAdvancedSearchForm) searchForm;
            if (!isEmpty(advancedSearchForm.getF73_name())) {
                boolQueryBuilder.must(buildStringKeywordQuery(advancedSearchForm.getF73_name_mc(), APPLICANTS + "." + FREEFORM_NAME, advancedSearchForm.getF73_name()));
            }

            if (!isEmpty(advancedSearchForm.getF73_address())) {
                boolQueryBuilder.must(buildStringKeywordQuery(CONTAINS, APPLICANTS + "." + FREEFORM_ADDRESS, advancedSearchForm.getF73_address()));
            }
            if(!isEmpty(advancedSearchForm.getF51_1()) || !isEmpty(advancedSearchForm.getF51_2())){
                boolQueryBuilder.must(buildClassificationQuery(LOCARNO_CLASSIFICATION, advancedSearchForm.getF51_1(), advancedSearchForm.getF51_2()));
            }

            if (!isEmpty(advancedSearchForm.getF45_year()) || !isEmpty(advancedSearchForm.getF45_bulletin_no())) {
                boolQueryBuilder.must(buildPublicationQuery(REGISTRATION_PUBLICATION_IDENTIFIER, advancedSearchForm.getF45_year(), advancedSearchForm.getF45_bulletin_no()));
            }

            if (!isEmpty(advancedSearchForm.getF74_name())) {
                boolQueryBuilder.must(buildStringKeywordQuery(advancedSearchForm.getF74_name_mc(), REPRESENTATIVES + "." + FREEFORM_NAME, advancedSearchForm.getF74_name()));
            }
            if (!isEmpty(advancedSearchForm.getF74_address())) {
                boolQueryBuilder.must(buildStringKeywordQuery(CONTAINS, REPRESENTATIVES + "." + FREEFORM_ADDRESS, advancedSearchForm.getF74_address()));
            }
        }

        if(log.isDebugEnabled()){
            log.debug(boolQueryBuilder.toString());
        }
        return boolQueryBuilder;
    }



}
