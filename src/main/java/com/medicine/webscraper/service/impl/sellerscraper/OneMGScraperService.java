package com.medicine.webscraper.service.impl.sellerscraper;

import static java.lang.String.format;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.medicine.webscraper.enums.Seller;
import com.medicine.webscraper.model.SellerPrice;
import com.medicine.webscraper.model.SellerPriceBuilder;
import com.medicine.webscraper.service.SellerScraperService;

@Service
public class OneMGScraperService implements SellerScraperService {

	private static final Logger LOGGER = LoggerFactory.getLogger(OneMGScraperService.class);

	@Override
	public SellerPrice getPrice(String buyURL) {
		LOGGER.info("OneMGScraperService :: getPrice :: started:: {}", buyURL);

		SellerPrice oneMGPrice = null;
		try {
			Document buyPage = Jsoup.connect(buyURL).get();

			String price = buyPage.getElementsByClass("DrugPriceBox__best-price___32JXw").text();
			String manu = buyPage.getElementsByClass("DrugHeader__meta-value___vqYM0").get(0).select("a[href]").text();
			String medName = buyPage.getElementsByClass("DrugHeader__left___19WY-").first().child(0).text();
			String subQty = buyPage.getElementsByClass("DrugPriceBox__quantity___2LGBX").text();
			String logoURL = buyPage.getElementsByClass("styles__logo-link-new___2YHUJ styles__button-text___3JQsb").first().child(0).attr("src");

			oneMGPrice = SellerPriceBuilder.builder()
										   .setPrice(Double.parseDouble(price.replace("₹", "")))
										   .setSellerMedName(medName)
										   .setBuyURL(buyURL)
										   .setSeller(getSeller())
										   .setManufacturer(manu)
										   .setSellerLogoURL(logoURL)
										   .setSubQty(subQty)
										   .build();

		} catch (IOException e) {
			LOGGER.error("OneMGScraperService :: getPrice :: error", e);
			return null;
		} catch (Exception e) {
			LOGGER.warn("Error in scraping data from {}", getSeller().getName(), e);
		}
		return oneMGPrice;
	}

	@Override
	public Seller getSeller() {
		return Seller.ONEMG;
	}

}
