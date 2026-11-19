package lt.gov.vpb.pdb.repository.elastic.dispute;

import lombok.extern.slf4j.Slf4j;
import lt.gov.vpb.pdb.form.DisputeSearchForm;
import lt.gov.vpb.pdb.model.Dispute;
import lt.gov.vpb.pdb.model.DisputeListItem;
import lt.gov.vpb.pdb.model.PaginatedList;
import lt.gov.vpb.pdb.repository.SearchService;
import lt.gov.vpb.pdb.repository.elastic.util.converter.MapToDisputeConverter;
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
import static lt.gov.vpb.pdb.repository.elastic.util.FieldDefinitions.DisputeFields.APPLICATION_DATE;
import static lt.gov.vpb.pdb.repository.elastic.util.FieldDefinitions.DisputeFields.REF_APPLICATION_NUMBER;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@Component("disputeSearchProvider")
@Slf4j
public class DisputeElasticSearchService implements SearchService<DisputeSearchForm, DisputeListItem, Dispute> {

    @Autowired
    TransportClient client;

    @Value("${elasticsearch.dispute-index}")
    String index;

    @Override
    public PaginatedList<DisputeListItem> search(DisputeSearchForm searchForm) {

        SearchRequestBuilder requestBuilder = client.prepareSearch(index)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(DisputeQueryBuilderUtils.buildQuery(searchForm))
                .setFrom(searchForm.getOffset()).setSize(searchForm.getSize());

        if(isEmpty(searchForm.getSortField())) {
            requestBuilder.addSort(APPLICATION_DATE, SortOrder.DESC);
            requestBuilder.addSort(REF_APPLICATION_NUMBER + ".keyword", SortOrder.DESC);
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

        return new PaginatedList<DisputeListItem>(
                MapToDisputeConverter.convert(mapList),
                response.getHits().totalHits,
                searchForm.getOffset(),
                searchForm.getSize());
    }

    @Override
    public Dispute details(String id) {
        GetResponse response = client.prepareGet().setIndex(index).setId(id).get();
        return MapToDisputeConverter.convert(response.getSourceAsMap());
    }
}
