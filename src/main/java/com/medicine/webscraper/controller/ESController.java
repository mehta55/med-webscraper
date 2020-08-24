package com.medicine.webscraper.controller;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.medicine.webscraper.enums.ElasticIndexData;
import com.medicine.webscraper.model.MedData;
import com.medicine.webscraper.service.ElasticSearchService;

@RestController
@RequestMapping("es")
public class ESController {

	@Autowired
	private ElasticSearchService service;

	@PostMapping("/index/{indexName}")
	public ResponseEntity<Void> createIndex(@PathVariable String indexName) {

		Optional<ElasticIndexData> targetIndex = ElasticIndexData.getByIndexName(indexName);

		if (targetIndex.isPresent()) {
			boolean success = service.createIndex(targetIndex.get());
			URI location = ServletUriComponentsBuilder.fromCurrentRequest()
					.buildAndExpand(ElasticIndexData.MED_DATA_INDEX).toUri();
			return success ? ResponseEntity.created(location).build()
					: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/index/{indexName}/data")
	public ResponseEntity<Void> saveToIndex(@PathVariable String indexName, @RequestBody Object data) {

		Optional<ElasticIndexData> targetIndex = ElasticIndexData.getByIndexName(indexName);

		if (targetIndex.isPresent()) {
			service.saveDataToES(data, targetIndex.get());
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}
