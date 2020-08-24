package com.medicine.webscraper.service.factory;

import static java.lang.String.format;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.medicine.webscraper.service.SellerScraperService;

@Service
public class SellerScraperFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(SellerScraperFactory.class);

	@Autowired
	private List<SellerScraperService> services;
	
	private Map<String, SellerScraperService> registeredServices;
	
	@PostConstruct
	public void registerServices() {
		LOGGER.info("SellerScraperFactory :: registerServices :: started");
		
		registeredServices = new HashMap<>();
		
		services.forEach(service -> {
			registeredServices.put(service.getSeller().getDomain(), service);
		});
	}
	
	public SellerScraperService getScraperService(String domain) {
		Assert.notNull(domain, "Domain name cannot be null!!");
		SellerScraperService service =  registeredServices.get(domain);
		if(Objects.isNull(service)) {
			LOGGER.warn(format("No Service registered for this domain: %s", domain));
		}
		return service;
	}
}
