package com.medicine.webscraper.controller;

import static com.medicine.webscraper.utils.Constants.FAILURE;
import static java.lang.String.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.medicine.webscraper.model.Medicine;
import com.medicine.webscraper.model.response.FetchPriceResponse;
import com.medicine.webscraper.model.response.SearchResponse;
import com.medicine.webscraper.service.MedicineService;

@RestController
@RequestMapping("medicine")
public class MedController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MedController.class);

	@Autowired
	private MedicineService medService;

	@GetMapping("scrape")
	public ResponseEntity<String> scrapeMedicines(String id) {
		LOGGER.info("MedController :: scrapeMedicines :: started");

		final Long scrapedMedCount = medService.scrapeMedicinesFromWeb();
		return ResponseEntity.ok().body(format("Successfully scraped data for %d medicines", scrapedMedCount));
	}

	@GetMapping("search")
	public ResponseEntity<SearchResponse> searchMeds(@RequestParam String keyword) {
		if(keyword.length() < 3) {
			return ResponseEntity.badRequest().body(new SearchResponse(FAILURE, "Minimum Search keyword length is 3"));
		}
		return ResponseEntity.ok(medService.searchMeds(keyword));
	}
	
	@GetMapping("price")
	public ResponseEntity<FetchPriceResponse> getMedPrice(@RequestParam String id) {
		return ResponseEntity.ok(medService.getMedPrice(id));
	}
}
