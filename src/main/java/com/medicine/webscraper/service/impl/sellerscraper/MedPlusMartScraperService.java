package com.medicine.webscraper.service.impl.sellerscraper;

import java.io.IOException;
import java.security.spec.MGF1ParameterSpec;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.medicine.webscraper.enums.Seller;
import com.medicine.webscraper.model.SellerPrice;
import com.medicine.webscraper.model.SellerPriceBuilder;
import com.medicine.webscraper.service.SellerScraperService;


@Service
public class MedPlusMartScraperService implements SellerScraperService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PractoScraperService.class);

	@Override
	public SellerPrice getPrice(String buyURL) {
		LOGGER.info("MedPlusMartScraperService :: getPrice :: started :: {}", buyURL);
		
		SellerPrice medPlusPrice = null;
		try {
			Document buyPage = Jsoup.connect(buyURL).get();

			String subQty = buyPage.getElementsByClass("p-rel").get(2).getElementsByTag("p").first().text();
			String priceQty = buyPage.getElementsByClass("p-rel").get(2).getElementsByTag("h3").first().text();
			String price = priceQty.replace(subQty, "").replace("MRP", "").trim();
			String medName = buyPage.getElementsByClass("productName hide").first().text();
			Optional<Element> mfgElement = buyPage.getElementsByTag("a").stream().filter(a -> a.text().contains("Mfg")).findFirst();
			String mfg = mfgElement.isPresent() ? mfgElement.get().text().replace("Mfg:", "").trim() : null;

			medPlusPrice = SellerPriceBuilder.builder()
								.setPrice(Double.parseDouble(price.replace("Rs.", "")))
								.setSellerMedName(medName)
								.setBuyURL(buyURL)
								.setSeller(getSeller())
								.setManufacturer(mfg)
								.build();
		
		} catch (IOException e) {
			LOGGER.error("MedPlusMartScraperService :: getPrice :: error", e);
			return null;
		} catch (Exception e) {
			LOGGER.warn("Error in scraping data from {}", getSeller().getName(), e);
		}
		return medPlusPrice;
	}

	@Override
	public Seller getSeller() {
		return Seller.MEDPLUSMART;
	}

	
}