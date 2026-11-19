package lt.gov.vpb.pdb.repository.elastic.niceclassification;

import lombok.extern.slf4j.Slf4j;
import lt.gov.vpb.pdb.form.NiceClassificationSearchForm;
import lt.gov.vpb.pdb.repository.elastic.util.EleasticQueryBuilderUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import static lt.gov.vpb.pdb.repository.elastic.util.FieldDefinitions.NiceClassificationFields.*;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.springframework.util.StringUtils.isEmpty;

@Slf4j
public class NiceClassificationQueryBuilderUtils extends EleasticQueryBuilderUtils {

    public static final String STATUS_STANDARD_CLARIFY = "STANDARD_CLARIFY";
    public static final String STATUS_STANDARD = "STANDARD";

    public static QueryBuilder buildQuery(NiceClassificationSearchForm searchForm) {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (!isEmpty(searchForm.getClass_number())) {
            boolQueryBuilder.must(matchPhraseQuery(CLASS_NUMBER, searchForm.getClass_number()));
        }

        if (!isEmpty(searchForm.getFirst_letter())) {
            boolQueryBuilder.must(boolQuery()
                    .should(wildcardQuery(TERM_LT + ".keyword", searchForm.getFirst_letter() + "*"))
                    .should(wildcardQuery(TERM_LT + "_lc.keyword", searchForm.getFirst_letter().toLowerCase() + "*")));
        }

        if (!isEmpty(searchForm.getClass_term_lt())) {
            boolQueryBuilder.must(buildStringKeywordQuery(searchForm.getClass_term_lt_mc(), TERM_LT, searchForm.getClass_term_lt()));
        }

        if (!isEmpty(searchForm.getClass_term_en())) {
            boolQueryBuilder.must(buildStringKeywordQuery(searchForm.getClass_term_en_mc(), TERM_EN, searchForm.getClass_term_en()));
        }

        if (!isEmpty(searchForm.getBasic_number())) {
            boolQueryBuilder.must(buildStringKeywordQuery(searchForm.getBasic_number_mc(), TERM_NUMBER, searchForm.getBasic_number()));
        }

        boolQueryBuilder.must(matchPhraseQuery(VERSION_YEAR, searchForm.getVersion_year()));

        boolQueryBuilder.must(boolQuery()
                .should(matchPhraseQuery(STATUS, STATUS_STANDARD))
                .should(matchPhraseQuery(STATUS, STATUS_STANDARD_CLARIFY))
        );

        if(log.isDebugEnabled()){
            log.debug(boolQueryBuilder.toString());
        }
        return boolQueryBuilder;
    }



}
