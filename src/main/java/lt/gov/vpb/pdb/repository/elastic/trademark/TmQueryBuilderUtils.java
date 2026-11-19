package lt.gov.vpb.pdb.repository.elastic.trademark;

import lombok.extern.slf4j.Slf4j;
import lt.gov.vpb.pdb.form.TmAdvancedSearchForm;
import lt.gov.vpb.pdb.form.TmSearchForm;
import lt.gov.vpb.pdb.model.util.StringMatching;
import lt.gov.vpb.pdb.repository.elastic.util.EleasticQueryBuilderUtils;
import org.elasticsearch.index.query.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static lt.gov.vpb.pdb.model.util.StringMatching.CONTAINS;
import static lt.gov.vpb.pdb.model.util.StringMatching.EQUALS;
import static lt.gov.vpb.pdb.repository.elastic.util.FieldDefinitions.PersonFields.FREEFORM_ADDRESS;
import static lt.gov.vpb.pdb.repository.elastic.util.FieldDefinitions.PersonFields.FREEFORM_NAME;
import static lt.gov.vpb.pdb.repository.elastic.util.FieldDefinitions.TrademarkFields.*;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.springframework.util.StringUtils.isEmpty;

@Slf4j
public class TmQueryBuilderUtils extends EleasticQueryBuilderUtils {

    static final String TY_FEATURE_COMBINED = "Combined";
    static final String TY_FEATURE_FIGURATIVE = "Figurative";

    public static QueryBuilder buildQuery(TmSearchForm searchForm) {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (!isEmpty(searchForm.getF540())) {
            boolQueryBuilder.must(buildStringKeywordQuery(searchForm.getF540_mc(), DESCRIPTION, searchForm.getF540()));
        }
        if (!isEmpty(searchForm.getTyfeature())) {
            if (TY_FEATURE_FIGURATIVE.equals(searchForm.getTyfeature())) {
                boolQueryBuilder.must(
                        boolQuery()
                                .should(matchQuery(TYFEATURE, TY_FEATURE_FIGURATIVE))
                                .should(matchQuery(TYFEATURE, TY_FEATURE_COMBINED))
                );
            } else {
                boolQueryBuilder.must(matchQuery(TYFEATURE, searchForm.getTyfeature()));
            }
        }
        if (!isEmpty(searchForm.getTykind())) {
            boolQueryBuilder.must(matchQuery(TYKIND, searchForm.getTykind()));
        }
        if (!isEmpty(searchForm.getStatusEn())) {
            boolQueryBuilder.must(matchQuery(STATUS_EN + ".keyword", searchForm.getStatusEn()));
        }
        if (!isEmpty(searchForm.getStatusLt())) {
            boolQueryBuilder.must(matchQuery(STATUS_LT + ".keyword", searchForm.getStatusLt()));
        }

        if (!isEmpty(searchForm.getF210())) {
            //toUpperCase as applicationnumber also contains letters, and in Elastic we do not keep data in additional _lc (lowercased) field for this
            boolQueryBuilder.must(buildStringKeywordQuery(searchForm.getF210_mc(), APPLICATION_NUMBER, searchForm.getF210().toUpperCase()));
        }
        if (!isEmpty(searchForm.getF111())) {
            boolQueryBuilder.must(buildStringKeywordQuery(searchForm.getF111_mc(), REGISTRATION_NUMBER, searchForm.getF111()));
        }

        if (!isEmpty(searchForm.getF220_from())) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(APPLICATION_DATE).timeZone(TIMEZONE_OFFSET).from(searchForm.getF220_from()));
        }
        if (!isEmpty(searchForm.getF220_to())) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(APPLICATION_DATE).timeZone(TIMEZONE_OFFSET).to(searchForm.getF220_to()));
        }

        if (!isEmpty(searchForm.getF181_from())) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(EXPIRY_DATE).timeZone(TIMEZONE_OFFSET).from(searchForm.getF181_from()));
        }
        if (!isEmpty(searchForm.getF181_to())) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(EXPIRY_DATE).timeZone(TIMEZONE_OFFSET).to(searchForm.getF181_to()));
        }

        if (searchForm instanceof TmAdvancedSearchForm) {

            TmAdvancedSearchForm advancedSearchForm = (TmAdvancedSearchForm) searchForm;
            if (!isEmpty(advancedSearchForm.getF732_name())) {
                boolQueryBuilder.must(buildStringKeywordQuery(advancedSearchForm.getF732_name_mc(), APPLICANTS + "." + FREEFORM_NAME, advancedSearchForm.getF732_name()));
            }

            if (!isEmpty(advancedSearchForm.getF732_address())) {
                boolQueryBuilder.must(buildStringKeywordQuery(CONTAINS, APPLICANTS + "." + FREEFORM_ADDRESS, advancedSearchForm.getF732_address()));
            }

            if (!isEmpty(advancedSearchForm.getF511_class()) && StringMatching.EQUALS == advancedSearchForm.getF511_class_mc()) {
                boolQueryBuilder.must(buildStringKeywordQuery(EQUALS, NICECLASSNUMBER, advancedSearchForm.getF511_class().replaceAll("[,; ]+", ", ")));
            }

            if (!isEmpty(advancedSearchForm.getF511_class()) && CONTAINS == advancedSearchForm.getF511_class_mc()) {
                List<String> numbers = Stream.of(advancedSearchForm.getF511_class().split("[,; ]+")).collect(Collectors.toList());
                numbers.forEach(niceClassNumber -> boolQueryBuilder.must(matchQuery(CLASSIFICATION_NICECLASSNUMBER, niceClassNumber)));
            }

            if (!isEmpty(advancedSearchForm.getF511_list())) {
                boolQueryBuilder.must(
                        ((BoolQueryBuilder) buildStringKeywordQuery(CONTAINS, CLASSIFICATION_DESCRIPTION, advancedSearchForm.getF511_list()))
                                .should(prefixQuery(CLASSIFICATION_DESCRIPTION, advancedSearchForm.getF511_list().toLowerCase()))
                );
            }

            if (!isEmpty(advancedSearchForm.getF531_1()) || !isEmpty(advancedSearchForm.getF531_2())) {
                boolQueryBuilder.must(buildClassificationQuery(VIENNA_CATEGORY_CODE, advancedSearchForm.getF531_1(), advancedSearchForm.getF531_2()));
            }

            if (!isEmpty(advancedSearchForm.getF442_year()) || !isEmpty(advancedSearchForm.getF442_bulletin_no())) {
                boolQueryBuilder.must(buildPublicationQuery(APPLICATION_PUBLICATION_IDENTIFIER, advancedSearchForm.getF442_year(), advancedSearchForm.getF442_bulletin_no()));
            }

            if (!isEmpty(advancedSearchForm.getF450_year()) || !isEmpty(advancedSearchForm.getF450_bulletin_no())) {
                boolQueryBuilder.must(buildPublicationQuery(REGISTRATION_PUBLICATION_IDENTIFIER, advancedSearchForm.getF450_year(), advancedSearchForm.getF450_bulletin_no()));
            }

            if (!isEmpty(advancedSearchForm.getF740_name())) {
                boolQueryBuilder.must(buildStringKeywordQuery(advancedSearchForm.getF740_name_mc(), REPRESENTATIVES + "." + FREEFORM_NAME, advancedSearchForm.getF740_name()));
            }
            if (!isEmpty(advancedSearchForm.getF740_address())) {
                boolQueryBuilder.must(buildStringKeywordQuery(CONTAINS, REPRESENTATIVES + "." + FREEFORM_ADDRESS, advancedSearchForm.getF740_address()));
            }
        }
        if (log.isDebugEnabled()) {
            log.debug(boolQueryBuilder.toString());
        }
        return boolQueryBuilder;
    }

}
