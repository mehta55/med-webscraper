package com.medicine.webscraper.enums;

import java.util.Arrays;
import java.util.Optional;

public enum ElasticIndexData {

	MED_DATA_INDEX("med_data_index", "/mappings/med_data_mapping.json");
	
	private String indexName;
	private String mapping;

	ElasticIndexData(String indexName, String mapping) {
		this.indexName = indexName;
		this.mapping = mapping;
	}

	public String getIndexName() {
		return indexName;
	}

	public String getMapping() {
		return mapping;
	}

	public static Optional<ElasticIndexData> getByIndexName(String incomingIndexName) {
		return Arrays.asList(ElasticIndexData.values()).stream()
				.filter(indexData -> incomingIndexName.equalsIgnoreCase(indexData.getIndexName()))
				.findFirst();
	}
}
