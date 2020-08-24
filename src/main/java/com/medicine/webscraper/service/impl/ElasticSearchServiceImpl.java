package com.medicine.webscraper.service.impl;

import static com.medicine.webscraper.utils.Constants.ID_FIELD;
import static com.medicine.webscraper.utils.Constants.SETTING_FILE_PATH;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.ScrollableHitSource.Hit;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicine.webscraper.enums.ElasticIndexData;
import com.medicine.webscraper.model.DataID;
import com.medicine.webscraper.service.ElasticSearchService;
import com.medicine.webscraper.utils.Commons;

@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchServiceImpl.class);

	@Autowired
	private RestHighLevelClient esClient;

	@Override
	public boolean createIndex(ElasticIndexData indexData) {
		LOGGER.info("ElasticSearchServiceImpl :: createIndex :: started");

		try {

			deleteIndexIfPresentAlready(indexData.getIndexName());

			String settingsFilePath = getClass().getResource(SETTING_FILE_PATH).getPath();
			String mappingFilePath = getClass().getResource(indexData.getMapping()).getPath();
			CreateIndexRequest request = new CreateIndexRequest(indexData.getIndexName())
					.settings(Commons.getFileAsSource(settingsFilePath), XContentType.JSON)
					.mapping(Commons.getFileAsSource(mappingFilePath), XContentType.JSON);

			CreateIndexResponse response = esClient.indices().create(request, RequestOptions.DEFAULT);

			return response.isAcknowledged();

		} catch (IOException e) {
			LOGGER.error("ElasticSearchServiceImpl :: createIndex :: error", e);
		}
		return false;
	}

	@Override
	public void saveBulkDataToES(List<Object> data, ElasticIndexData indexData) {
		LOGGER.info("ElasticSearchServiceImpl :: saveBulkDataToES :: started");

		Collection<List<Object>> dataChunks = splitListToChunks(data, 3000);

		dataChunks.forEach((chunk -> {

			BulkRequest request = new BulkRequest();
			chunk.forEach(unitData -> {

				try {

					String src = new ObjectMapper().writeValueAsString(unitData);
					String srcID = new ObjectMapper().readValue(src, DataID.class).getId();

					request.add(new IndexRequest(indexData.getIndexName()).source(src, XContentType.JSON).id(srcID));

				} catch (JsonProcessingException e) {
					LOGGER.info("ElasticSearchServiceImpl :: saveBulkDataToES :: error :: {}", e.getMessage());

				}
			});

			try {

				BulkResponse response = esClient.bulk(request, RequestOptions.DEFAULT);

			} catch (IOException e) {
				LOGGER.info("ElasticSearchServiceImpl :: saveBulkDataToES :: error :: {}", e.getMessage());
			}

		}));

	}

	private Collection<List<Object>> splitListToChunks(List<Object> data, Integer size) {
		final AtomicInteger counter = new AtomicInteger();
		return data.stream().collect(Collectors.groupingBy(it -> counter.getAndIncrement() / size)).values();
	}

	private boolean deleteIndexIfPresentAlready(String indexName) throws IOException {
		LOGGER.info("ElasticSearchServiceImpl :: deleteIndexIfPresentAlready :: started");

		if (!doesIndexExist(indexName)) {
			return false;
		}

		DeleteIndexRequest deleteRequest = new DeleteIndexRequest(indexName);
		try {
			return esClient.indices().delete(deleteRequest, RequestOptions.DEFAULT).isAcknowledged();
		} catch (IOException e) {
			LOGGER.error("Error while deleting index {}", indexName, e);
		}

		return false;
	}

	private boolean doesIndexExist(String indexName) throws IOException {
		LOGGER.info("ElasticSearchServiceImpl :: doesIndexExist :: started");

		GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
		return esClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
	}

	@Override
	public void saveDataToES(Object data, ElasticIndexData indexData) {

		try {
			String src = new ObjectMapper().writeValueAsString(data);
			String srcID = new ObjectMapper().readValue(src, DataID.class).getId();
			IndexRequest request = new IndexRequest(indexData.getIndexName()).source(src, XContentType.JSON).id(srcID);
			IndexResponse response = esClient.index(request, RequestOptions.DEFAULT);

		} catch (IOException e) {

		}

	}

	@Override
	public SearchResponse executeESQuery(QueryBuilder boolQueryBuilder, int from, int size,
			ElasticIndexData indexData) {
		LOGGER.info("ElasticSearchServiceImpl :: executeESQuery :: start");

		SearchRequest searchRequest = new SearchRequest(indexData.getIndexName());
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.size(size);
		searchRequest.source(searchSourceBuilder);
		SearchResponse searchResponse = null;
		try {
			searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			LOGGER.error("Error occurred while executing ES Query : {}", e.getMessage());
		}

		LOGGER.info("ElasticSearchServiceImpl :: executeESQuery :: exit :: response- {}",
				Objects.nonNull(searchResponse) ? searchResponse.getHits().getTotalHits().value : null);
		return searchResponse;
	}

	@Override
	public Map<String, Object> getDocByID(String id, ElasticIndexData indexData) {
		LOGGER.info("ElasticSearchServiceImpl :: getDocByID :: start :: {}-{}", indexData.getIndexName(), id);

		Map<String, Object> doc = new HashMap<>();
		SearchRequest searchRequest = new SearchRequest(indexData.getIndexName());
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		BoolQueryBuilder searchQuery = QueryBuilders.boolQuery();
		searchQuery.must(QueryBuilders.termQuery(ID_FIELD, id));
		searchSourceBuilder.query(searchQuery);
		searchRequest.source(searchSourceBuilder);
		SearchResponse searchResponse = null;
		try {
			searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			LOGGER.error("Error occurred while executing ES Query : {}", e.getMessage());
		}

		if (Objects.nonNull(searchResponse)) {
			SearchHit[] hits = searchResponse.getHits().getHits();
			if (hits.length == 1) {
				doc = hits[0].getSourceAsMap();
			}
		}

		LOGGER.info("ElasticSearchServiceImpl :: getDocByID :: exit :: {}", doc);
		return doc;
	}
}
