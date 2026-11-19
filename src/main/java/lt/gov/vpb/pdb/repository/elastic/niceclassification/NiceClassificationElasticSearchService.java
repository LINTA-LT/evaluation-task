package lt.gov.vpb.pdb.repository.elastic.niceclassification;

import lt.gov.vpb.pdb.form.NiceClassificationSearchForm;
import lt.gov.vpb.pdb.model.Design;
import lt.gov.vpb.pdb.model.Details;
import lt.gov.vpb.pdb.model.NiceClassificationListItem;
import lt.gov.vpb.pdb.model.PaginatedList;
import lt.gov.vpb.pdb.repository.SearchService;
import lt.gov.vpb.pdb.repository.elastic.util.converter.MapToDesignConverter;
import lt.gov.vpb.pdb.repository.elastic.util.converter.MapToNiceClassificationConverter;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static lt.gov.vpb.pdb.repository.elastic.util.FieldDefinitions.NiceClassificationFields.TERM_LT;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@Component("niceClassificationSearchProvider")
public class NiceClassificationElasticSearchService implements SearchService<NiceClassificationSearchForm, NiceClassificationListItem, Details> {

    @Autowired
    TransportClient client;

    @Value("${elasticsearch.niceclass-index}")
    String index;

    @Override
    public PaginatedList<NiceClassificationListItem> search(NiceClassificationSearchForm searchForm){

        SearchRequestBuilder requestBuilder = client.prepareSearch(index)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(NiceClassificationQueryBuilderUtils.buildQuery(searchForm))
                .setFrom(searchForm.getOffset()).setSize(searchForm.getSize());

        if(isEmpty(searchForm.getSortField())) {
            requestBuilder.addSort( TERM_LT + "_lc.keyword", SortOrder.ASC);
        } else {
            requestBuilder.addSort(searchForm.getSortField(), SortOrder.fromString(searchForm.getSortDir()));
        }

        SearchResponse response = requestBuilder
                .setExplain(true)
                .get();

        SearchHit[] searchHits = response.getHits().getHits();
        List<Map<String, Object>> mapList = stream(searchHits)
                .map(searchHit -> searchHit.getSourceAsMap())
                .collect(Collectors.toList());

        return new PaginatedList<NiceClassificationListItem>(
                MapToNiceClassificationConverter.convert(mapList),
                response.getHits().totalHits,
                searchForm.getOffset(),
                searchForm.getSize());
    }

    @Override
    public Design details(String id) {
        GetResponse response = client.prepareGet().setIndex(index).setId(id).get();
        return MapToDesignConverter.convert(response.getSourceAsMap());

    }
}
