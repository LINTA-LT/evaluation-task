package lt.gov.vpb.pdb.repository.elastic.dispute;

import lombok.extern.slf4j.Slf4j;
import lt.gov.vpb.pdb.form.DisputeSearchForm;
import lt.gov.vpb.pdb.repository.elastic.util.EleasticQueryBuilderUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import static lt.gov.vpb.pdb.repository.elastic.util.FieldDefinitions.DisputeFields.*;
import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.springframework.util.StringUtils.isEmpty;

@Slf4j
public class DisputeQueryBuilderUtils extends EleasticQueryBuilderUtils {

    public static QueryBuilder buildQuery(DisputeSearchForm searchForm) {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(matchPhraseQuery(DISPUTE_TYPE, searchForm.getType().ordinal() + 1));

        if (!isEmpty(searchForm.getRefapplicationnumber())) {
            boolQueryBuilder.must(buildStringKeywordQuery(searchForm.getRefapplicationnumber_mc(), REF_APPLICATION_NUMBER, searchForm.getRefapplicationnumber()));
        }

        if (!isEmpty(searchForm.getDate_from())) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(DECISION_EFFECTIVE_DATE).timeZone(TIMEZONE_OFFSET).from(searchForm.getDate_from()));
        }
        if (!isEmpty(searchForm.getDate_to())) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(DECISION_EFFECTIVE_DATE).timeZone(TIMEZONE_OFFSET).to(searchForm.getDate_to()));
        }

        if (!isEmpty(searchForm.getDecision_number())) {
            boolQueryBuilder.must(buildStringKeywordQuery(searchForm.getDecision_number_mc(), CASE_NUMBER, searchForm.getDecision_number()));
        }
        if (!isEmpty(searchForm.getF540())) {
            boolQueryBuilder.must(buildStringKeywordQuery(searchForm.getF540_mc(), VERBAL_ELEMENT, searchForm.getF540()));
        }
        if (!isEmpty(searchForm.getF210())) {
            boolQueryBuilder.must(buildStringKeywordQuery(searchForm.getF210_mc(), APPLICATION_NUMBER, searchForm.getF210()));
        }
        if (!isEmpty(searchForm.getF111())) {
            boolQueryBuilder.must(buildStringKeywordQuery(searchForm.getF111_mc(), REGISTRATION_NUMBER, searchForm.getF111()));
        }

        if(ArrayUtils.isNotEmpty(searchForm.getF511())){
            for (String niceClass : searchForm.getF511()) {
                boolQueryBuilder.must(matchPhraseQuery(NICE_CLASSIFICATION, niceClass));
            }
        }

        if (!isEmpty(searchForm.getGrounds())) {
            //multiple CSV
            if(searchForm.getGrounds().contains(",")){
                String[] grounds = searchForm.getGrounds().split(",");
                for(String groundsItem : grounds){
                    boolQueryBuilder.must(matchPhraseQuery(GROUNDS, groundsItem));
                }
            } else {
                boolQueryBuilder.must(matchPhraseQuery(GROUNDS, searchForm.getGrounds()));
            }
        }

        if (!isEmpty(searchForm.getDecision())) {
            boolQueryBuilder.must(matchPhraseQuery(DECISION_TAKEN, searchForm.getDecision()));
        }

        if (!isEmpty(searchForm.getComments())) {
            boolQueryBuilder.must(buildStringKeywordQuery(searchForm.getComments_mc(), COMMENTS, searchForm.getComments(), false));
        }

        if(log.isDebugEnabled()){
            log.debug(boolQueryBuilder.toString());
        }
        return boolQueryBuilder;
    }



}
