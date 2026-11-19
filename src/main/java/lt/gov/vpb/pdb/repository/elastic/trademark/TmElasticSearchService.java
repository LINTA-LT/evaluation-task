package lt.gov.vpb.pdb.repository.elastic.trademark;

import lombok.extern.slf4j.Slf4j;
import lt.gov.vpb.pdb.form.TmAdvancedSearchForm;
import lt.gov.vpb.pdb.form.TmSearchForm;
import lt.gov.vpb.pdb.model.PaginatedList;
import lt.gov.vpb.pdb.model.Trademark;
import lt.gov.vpb.pdb.model.TrademarkListItem;
import lt.gov.vpb.pdb.repository.SearchService;
import lt.gov.vpb.pdb.repository.elastic.util.converter.MapToTrademarkConverter;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static lt.gov.vpb.pdb.repository.elastic.util.FieldDefinitions.TrademarkFields.*;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@Component("tmSearchProvider")
@Slf4j
public class TmElasticSearchService implements SearchService<TmSearchForm, TrademarkListItem, Trademark> {

    @Autowired
    TransportClient client;

    @Value("${elasticsearch.trademark-index}")
    String index;

    @Override
    public PaginatedList<TrademarkListItem> search(TmSearchForm searchForm){

        SearchRequestBuilder requestBuilder = client.prepareSearch(index)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(TmQueryBuilderUtils.buildQuery(searchForm))
                .setFrom(searchForm.getOffset()).setSize(searchForm.getSize());

        if(isEmpty(searchForm.getSortField())) {
            requestBuilder.addSort(APPLICATION_DATE, SortOrder.DESC);
            requestBuilder.addSort(APPLICATION_NUMBER + ".keyword", SortOrder.DESC);
        } else if (searchForm instanceof TmAdvancedSearchForm
                        && !isEmpty(((TmAdvancedSearchForm)searchForm).getF450_year())
                        && !isEmpty(((TmAdvancedSearchForm)searchForm).getF450_bulletin_no())){
                requestBuilder.addSort(searchForm.getSortField(), SortOrder.fromString(searchForm.getSortDir()));
                requestBuilder.addSort(REGISTRATION_NUMBER_NUMERIC, SortOrder.DESC);
        } else if((APPLICATION_NUMBER + ".keyword").equals(searchForm.getSortField())){
            requestBuilder.addSort(APPLICATION_DATE, SortOrder.fromString(searchForm.getSortDir()));
            requestBuilder.addSort(APPLICATION_NUMBER + ".keyword", SortOrder.fromString(searchForm.getSortDir()));
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

        return new PaginatedList<TrademarkListItem>(
                MapToTrademarkConverter.convert(mapList),
                response.getHits().totalHits,
                searchForm.getOffset(),
                searchForm.getSize());
    }

    @Override
    public Trademark details(String id) {
        GetResponse response = client.prepareGet().setIndex(index).setId(id).get();
        if(response.isExists()){
            return MapToTrademarkConverter.convert(response.getSourceAsMap());
        } else {
            log.warn("Trademark with identifier {} not found", id);
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
    }

}
