package com.medicine.webscraper.service;

import com.medicine.webscraper.model.response.FetchPriceResponse;
import com.medicine.webscraper.model.response.SearchResponse;

public interface MedicineService {
	
	public Long scrapeMedicinesFromWeb();
	
	public SearchResponse searchMeds(String keyword);
	
	public FetchPriceResponse getMedPrice(String id);

}
