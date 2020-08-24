package com.medicine.webscraper.model;

import com.medicine.webscraper.utils.Commons;

public class SellerPriceBuilder {

	private SellerPrice obj;

	private SellerPriceBuilder() {
		obj = new SellerPrice();
		obj.setSeller(new Seller());
		obj.setQty(1);
	}

	public SellerPriceBuilder setSellerMedName(String medName) {
		obj.setSellerMedName(medName);
		return this;
	}

	public SellerPriceBuilder setPrice(Double price) {
		obj.setPrice(price);
		return this;
	}

	public SellerPriceBuilder setBuyURL(String buyURL) {
		obj.setBuyURL(buyURL);
		return this;
	}

	public SellerPriceBuilder setImageURL(String imageURL) {
		obj.setImageURL(imageURL);
		return this;
	}

	public SellerPriceBuilder setQty(Integer qty) {
		obj.setQty(qty);
		return this;
	}

	public SellerPriceBuilder setSeller(com.medicine.webscraper.enums.Seller seller) {
		obj.getSeller().setName(seller.getName());
		obj.getSeller().setDomain(seller.getDomain());
		return this;
	}

	public SellerPriceBuilder setManufacturer(String manufacturer) {
		obj.setManufacturer(manufacturer);
		return this;
	}

	public SellerPriceBuilder setSubQty(String subQty) {
		obj.setSubQty(subQty);
		return this;
	}

	public SellerPriceBuilder setSellerLogoURL(String logoURL) {
		obj.getSeller().setLogoURL(logoURL);
		return this;
	}

	public static SellerPriceBuilder builder() {
		return new SellerPriceBuilder();
	}

	public SellerPrice build() {
		obj.setLastModifiedOn(Commons.getCurrentDateTime());
		return obj;
	}
}
