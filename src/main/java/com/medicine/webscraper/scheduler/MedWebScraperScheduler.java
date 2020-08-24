package com.medicine.webscraper.scheduler;

import static com.medicine.webscraper.utils.Constants.GOOGLE_SEARCH;
import static com.medicine.webscraper.utils.Commons.encoded;
import static java.lang.String.format;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.medicine.webscraper.enums.Seller;
import com.medicine.webscraper.model.MedData;
import com.medicine.webscraper.model.Medicine;
import com.medicine.webscraper.model.SellerPrice;
import com.medicine.webscraper.repository.MedicineRepo;
import com.medicine.webscraper.service.factory.SellerScraperFactory;
import com.medicine.webscraper.utils.Commons;

@Service
public class MedWebScraperScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(MedWebScraperScheduler.class);

	@Autowired
	private SellerScraperFactory scraperFactory;
	
	@Autowired
	private MedicineRepo medRepo;

//	@PostConstruct
	public void startScraping() {
		String medName = "DIVA HMG 75IU INJ";
		String id = UUID.randomUUID().toString();
		System.out.println(startScraping(new MedData(id, medName)));
	}
	
	
	public Medicine startScraping(MedData med) {
		LOGGER.info("MedWebScraperScheduler :: startScraping :: started :: {}", med.getMedName());

	
		try {
			Document doc = Jsoup.connect(format(GOOGLE_SEARCH, encoded(med.getMedName())))
								.header("Accept-Encoding", "gzip, deflate")
								.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
								.maxBodySize(0)
								.timeout(600000)
								.get();
			
			Element searchResults = doc.getElementById("rso");
			Elements anchors = searchResults.select("a[href]");
			List<String> refrences = anchors.eachAttr("href");

			
			List<SellerPrice> priceList = refrences.stream()
					.filter(Seller::isSellerURL)
					.collect(Collectors.toMap(url -> url, url -> scraperFactory.getScraperService(Commons.getDomainFromURL(url))))
					.entrySet()
					.stream()
					.filter(service -> Objects.nonNull(service.getValue()))
					.map(service -> {
						try {
							return service.getValue().getPrice(service.getKey());							
						} catch (Exception e) {
							LOGGER.error(format("Error while scraping %s service", service.getValue().getSeller().getName()));
						}
						return null;
					})
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
			
			Medicine medicine = medRepo.save(new Medicine(med.getId(), med.getMedName(), priceList));
			return medicine;
		
		} catch (IOException e) {
			LOGGER.error("MedWebScraperScheduler :: startScraping :: error", e);
		}
		
		return null;
	}
}
