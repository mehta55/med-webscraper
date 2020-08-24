package com.medicine.webscraper.service.impl.sellerscraper;

import java.io.IOException;
import java.util.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.medicine.webscraper.enums.Seller;
import com.medicine.webscraper.model.SellerPrice;
import com.medicine.webscraper.model.SellerPriceBuilder;
import com.medicine.webscraper.service.SellerScraperService;

@Service
public class PharmEasyScraperService implements SellerScraperService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PharmEasyScraperService.class);

	@Override
	public SellerPrice getPrice(String buyURL) {
		LOGGER.info("PharmEasyScraperService :: getPrice :: started :: {}", buyURL);
		
		SellerPrice pharmEasyPrice = null;
		try {
			Document buyPage = Jsoup.connect(buyURL).get();

			String price = buyPage.getElementsByClass("_1_yM9").text();
			String imageURL = buyPage.getElementsByClass("_150ST").attr("src");
			String manu = buyPage.getElementsByClass("_3JVGI").text().replace("By ", "");
			String medName = buyPage.getElementsByClass("_2UHKQ").first().child(0).text();
			String subQty = buyPage.getElementsByClass("_36aef").text();
			String logoURL = buyPage.getElementsByClass("_536c").attr("src");
			
			pharmEasyPrice = SellerPriceBuilder.builder()
								.setPrice(Double.parseDouble(price.replace("₹", "").replace("*", "")))
								.setSellerMedName(medName)
								.setBuyURL(buyURL)
								.setImageURL(Objects.nonNull(imageURL) ? imageURL : null)
								.setSeller(getSeller())
								.setManufacturer(manu)
								.setSubQty(subQty)
								.setSellerLogoURL(logoURL)
								.build();
		
		} catch (IOException e) {
			LOGGER.error("PharmEasyScraperService :: getPrice :: error", e);
			return null;
		} catch (Exception e) {
			LOGGER.warn("Error in scraping data from {}", getSeller().getName(), e);
		}
		return pharmEasyPrice;
	}

	@Override
	public Seller getSeller() {
		return Seller.PHARMEASY;
	}

}
