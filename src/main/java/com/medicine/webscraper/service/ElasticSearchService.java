package com.medicine.webscraper.service;

import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;

import com.medicine.webscraper.enums.ElasticIndexData;

public interface ElasticSearchService {

	public boolean createIndex(ElasticIndexData indexData);
	
	public void saveBulkDataToES(List<Object> data, ElasticIndexData indexData);
	
	public void saveDataToES(Object data, ElasticIndexData indexData);
	
	public SearchResponse executeESQuery(QueryBuilder boolQueryBuilder, int from, int size, ElasticIndexData indexData);

	public Map<String, Object> getDocByID(String id, ElasticIndexData indexData);
}
