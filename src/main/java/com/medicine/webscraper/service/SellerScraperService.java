package com.medicine.webscraper.service;

import com.medicine.webscraper.enums.Seller;
import com.medicine.webscraper.model.SellerPrice;

public interface SellerScraperService {
	
	public SellerPrice getPrice(String buyURL);
	
	public Seller getSeller();

}
