package com.medicine.webscraper.service.impl.sellerscraper;

import java.io.IOException;

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
public class NetMedsScraperService implements SellerScraperService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NetMedsScraperService.class);

	@Override
	public SellerPrice getPrice(String buyURL) {
		LOGGER.info("NetMedsScraperService :: getPrice :: started :: {}", buyURL);
		
		SellerPrice netMedsPrice = null;
		try {
			Document buyPage = Jsoup.connect(buyURL).get();

			String price = buyPage.getElementById("total_amount").text();
			String imageURL = buyPage.getElementsByClass("product-image-photo").attr("src");
			String manu = buyPage.getElementsByClass("drug-manu").get(1).select("a[href]").text();
			String medName = buyPage.getElementsByClass("product-detail").first().child(0).text();
			String logoURL = buyPage.getElementsByClass("logo").first().child(0).child(0).attr("src");
			
			netMedsPrice = SellerPriceBuilder.builder()
								.setPrice(Double.parseDouble(price.replace("Rs.", "").replace(",", "")))
								.setSellerMedName(medName)
								.setBuyURL(buyURL)
								.setImageURL(imageURL)
								.setSeller(getSeller())
								.setManufacturer(manu)
								.setSellerLogoURL(logoURL)
								.build();
		
		} catch (IOException e) {
			LOGGER.error("NetMedsScraperService :: getPrice :: error", e);
			return null;
		} catch (Exception e) {
			LOGGER.warn("Error in scraping data from {}", getSeller().getName(), e);
		}
		return netMedsPrice;
	}

	@Override
	public Seller getSeller() {
		return Seller.NETMEDS;
	}

	
}
