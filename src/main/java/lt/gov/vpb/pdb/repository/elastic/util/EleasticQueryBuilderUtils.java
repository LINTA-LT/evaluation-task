package lt.gov.vpb.pdb.repository.elastic.util;

import lombok.extern.slf4j.Slf4j;
import lt.gov.vpb.pdb.model.util.StringMatching;
import lt.gov.vpb.pdb.repository.util.QueryBuilderHelper;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import static lt.gov.vpb.pdb.model.util.StringMatching.*;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.springframework.util.StringUtils.isEmpty;

@Slf4j
public class EleasticQueryBuilderUtils implements QueryBuilderHelper {

    protected  static final String TIMEZONE_OFFSET = "+03:00";

    public static QueryBuilder buildStringKeywordQuery(StringMatching sm, String field, String value, boolean keyword){
        if (sm == CONTAINS) {
            return boolQuery()
                    .should(wildcardQuery(keyword? field + ".keyword" : field, "*" + value + "*"))
                    .should(wildcardQuery(field + "_lc.keyword", "*" + value.toLowerCase() + "*"))
                    .should(matchPhraseQuery(field, value));
        } else if (sm == STARTS_WITH) {
            return boolQuery()
                    .should(wildcardQuery(keyword? field + ".keyword" : field, value + "*"))
                    .should(wildcardQuery(field + "_lc.keyword", value.toLowerCase() + "*"));
        } else if (sm == ENDS_WITH) {
            return boolQuery()
                    .should(wildcardQuery(keyword? field + ".keyword" : field, "*" + value))
                    .should(wildcardQuery(field + "_lc.keyword", "*" + value.toLowerCase()));
        } else { // EQUALS
            return boolQuery()
                    .should(matchPhraseQuery(keyword? field + ".keyword" : field, value))
                    .should(matchPhraseQuery(field + "_lc.keyword", value.toLowerCase()));
        }
    }

    public static QueryBuilder buildStringKeywordQuery(StringMatching sm, String field, String value) {
        return buildStringKeywordQuery(sm, field, value, true);
    }

    public static QueryBuilder buildPublicationQuery(String field, String year, String bulletinNo) {
        String yearStr =  !isBlank(year) ? year : "*";
        String bulletinNoStr = !isBlank(bulletinNo) ? bulletinNo : "*";
        BoolQueryBuilder boolQuery = boolQuery().should(wildcardQuery(field + ".keyword",  yearStr + "/" + bulletinNoStr));
        if(!isBlank(bulletinNo)){
            if(bulletinNo.length() == 2 && bulletinNo.startsWith("0")) {
                //add additional criteria with bulletin no. without leading zero
                boolQuery.should(wildcardQuery(field + ".keyword", yearStr + "/" + bulletinNo.substring(1, 2)));
            } else if(bulletinNo.length() == 1){
                //add additional criteria with bulletin no. with a leading zero
                boolQuery.should(wildcardQuery(field + ".keyword",  yearStr + "/0" + bulletinNo));
            }
        }
        return boolQuery;
    }

    /**
     * Build classification query. Supports 2 and 3 sections (xx.yy and xx.yy.zz) format
     * @param field
     * @param classValue
     * @param subclassValue
     * @return
     */
    public static QueryBuilder buildClassificationQuery(String field, String classValue, String subclassValue) {
        if(!isEmpty(classValue)) {
            if (classValue.length() == 1) {
                classValue = "0" + classValue;
            }
        } else {
            classValue = "??";
        }
        if(!isEmpty(subclassValue)) {
            if (subclassValue.length() == 1) {
                subclassValue = "0" + subclassValue;
            }
        } else {
            subclassValue = "??";
        }
        return boolQuery()
                .should(wildcardQuery(field, classValue + "." + subclassValue))
                .should(wildcardQuery(field, classValue + "." + subclassValue + ".??"));
    }
}
