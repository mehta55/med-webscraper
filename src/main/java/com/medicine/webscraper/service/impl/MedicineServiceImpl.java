package com.medicine.webscraper.service.impl;

import static com.medicine.webscraper.enums.ElasticIndexData.MED_DATA_INDEX;
import static com.medicine.webscraper.utils.Constants.FAILURE;
import static com.medicine.webscraper.utils.Constants.ID_FIELD;
import static com.medicine.webscraper.utils.Constants.MED_NAME_AUTOCOMPLETE_FIELD;
import static com.medicine.webscraper.utils.Constants.MED_NAME_FIELD;
import static com.medicine.webscraper.utils.Constants.MED_NAME_PREFIX_BOOST;
import static com.medicine.webscraper.utils.Constants.MED_NAME_TOKEN0_BOOST;
import static com.medicine.webscraper.utils.Constants.MED_NAME_TOKEN0_FIELD;
import static com.medicine.webscraper.utils.Constants.MED_NAME_TOKEN1_BOOST;
import static com.medicine.webscraper.utils.Constants.MED_NAME_TOKEN1_FIELD;
import static com.medicine.webscraper.utils.Constants.MED_SOURCE;
import static com.medicine.webscraper.utils.Constants.MED_SOURCE_FILTER;
import static com.medicine.webscraper.utils.Constants.STANDARD_ANALYZER;
import static com.medicine.webscraper.utils.Constants.SUCCESS;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.medicine.webscraper.model.MedData;
import com.medicine.webscraper.model.Medicine;
import com.medicine.webscraper.model.response.FetchPriceResponse;
import com.medicine.webscraper.model.response.SearchResponse;
import com.medicine.webscraper.scheduler.MedWebScraperScheduler;
import com.medicine.webscraper.service.ElasticSearchService;
import com.medicine.webscraper.service.MedicineService;

@Service
public class MedicineServiceImpl implements MedicineService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MedicineServiceImpl.class);

	@Autowired
	private ElasticSearchService elasticSearchService;
	
	@Autowired
	private MedWebScraperScheduler webScrapperService;

	@Override
	public Long scrapeMedicinesFromWeb() {

		LOGGER.info("MedicineServiceImpl :: scrapeMedicinesFromWeb :: started");
		Instant start = Instant.now();

		Long scrapedMedCount = 0L;
		boolean created = elasticSearchService.createIndex(MED_DATA_INDEX);
		if (created) {
			try {
				Document srcPage = Jsoup.connect(MED_SOURCE).get();
				Elements categoriesRefs = srcPage.getElementsByTag("li");

				List<Object> meds = categoriesRefs.stream()
						.map(element -> element.getElementsByTag("a").attr("href"))
						.filter(href -> href.contains(MED_SOURCE_FILTER))
						.map(this::getMedsForCategory)
						.flatMap(List::stream)
						.map(MedData::new)
						.collect(Collectors.toList());
				
				scrapedMedCount += meds.size();
				elasticSearchService.saveBulkDataToES(meds, MED_DATA_INDEX);

			} catch (IOException e) {
				LOGGER.error("MedicineServiceImpl :: scrapeMedicinesFromWeb :: error", e);
			}

		}

		LOGGER.info("MedicineServiceImpl :: scrapeMedicinesFromWeb :: exit :: scrapedData {} :: timeTaken {}",
				scrapedMedCount, Duration.between(start, Instant.now()).toMillis());

		return scrapedMedCount;
	}

	private List<String> getMedsForCategory(String categoryURL) {
		LOGGER.info("MedicineServiceImpl :: getMedsForCategory :: started :: {}", categoryURL);

		try {
			Document catePage = Jsoup.connect(categoryURL).get();
			Elements products = catePage.getElementsByClass("product-item");
			return products.stream().map(product -> product.text()).collect(Collectors.toList());

		} catch (IOException e) {
			LOGGER.error("MedicineServiceImpl :: getMedsForCategory :: error :: {}", e.getMessage());

		}

		return new ArrayList<>();
	}

	@Override
	public SearchResponse searchMeds(String keyword) {
		MedData searchMed = new MedData(keyword);
		QueryBuilder query = buildSearchQuery(searchMed);
		SearchHit[] hits = elasticSearchService.executeESQuery(query, 0, 10, MED_DATA_INDEX).getHits().getHits();
		
		List<MedData> meds = Arrays.asList(hits).parallelStream().map( hit -> {
			
			Map<String, Object> doc = hit.getSourceAsMap();
			MedData med = new MedData();
			med.setId(doc.get(ID_FIELD).toString());
			med.setMedName(doc.get(MED_NAME_FIELD).toString());
			return med;
		
		}).collect(Collectors.toList());
		
		
		return new SearchResponse(SUCCESS, meds);
	}
	
	private QueryBuilder buildSearchQuery(MedData searchMed) {
		LOGGER.debug("MedicineServiceImpl :: buildSearchQuery :: start");
		
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		if (!StringUtils.isEmpty(searchMed.getMedName())) {
			boolQuery.must(matchQuery(MED_NAME_AUTOCOMPLETE_FIELD, searchMed.getMedName())
					 .analyzer(STANDARD_ANALYZER))
					.should(QueryBuilders.prefixQuery(MED_NAME_FIELD, searchMed.getMedName())
							.boost(MED_NAME_PREFIX_BOOST))
					.should(QueryBuilders.termQuery(MED_NAME_TOKEN0_FIELD, searchMed.getMedNameToken0())
							.boost(MED_NAME_TOKEN0_BOOST))
					.should(QueryBuilders.termQuery(MED_NAME_TOKEN1_FIELD, searchMed.getMedNameToken1())
							.boost(MED_NAME_TOKEN1_BOOST));
		}
		
		LOGGER.info("MedicineServiceImpl :: buildSearchQuery :: exit :: {}", boolQuery);
		return boolQuery;
	}
	
	public FetchPriceResponse getMedPrice(String id) {
		LOGGER.info("MedicineServiceImpl :: getMedPrice :: start :: {}", id);
		
		Map<String, Object> doc = elasticSearchService.getDocByID(id, MED_DATA_INDEX);
		FetchPriceResponse response = null;
		
		if (Objects.nonNull(doc) && doc.containsKey(MED_NAME_FIELD)) {
			Medicine medicine = webScrapperService.startScraping(new MedData(id, doc.get(MED_NAME_FIELD).toString()));
			if(Objects.nonNull(medicine)) {
				response = new FetchPriceResponse(SUCCESS, medicine);
			}
			else {				
				response = new FetchPriceResponse(FAILURE, "Failed to fetch price for medicine");
			}
		} else {
			response = new FetchPriceResponse(FAILURE, "No medicine found by given id");
		}
		
		LOGGER.info("MedicineServiceImpl :: getMedPrice :: exit :: {}", response.getStatus());
		return response;
	}

}
