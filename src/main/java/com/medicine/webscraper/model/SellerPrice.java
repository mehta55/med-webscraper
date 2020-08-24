package com.medicine.webscraper.model;

import java.time.LocalDateTime;

public class SellerPrice {

	private Seller seller;
	private String sellerMedName;
	private String buyURL;
	private Double price;
	private Integer qty;
	private String subQty;
	private String imageURL;
	private String manufacturer;
	private LocalDateTime lastModifiedOn;

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public String getSellerMedName() {
		return sellerMedName;
	}

	public void setSellerMedName(String sellerMedName) {
		this.sellerMedName = sellerMedName;
	}

	public String getBuyURL() {
		return buyURL;
	}

	public void setBuyURL(String buyURL) {
		this.buyURL = buyURL;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public String getSubQty() {
		return subQty;
	}

	public void setSubQty(String subQty) {
		this.subQty = subQty;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public LocalDateTime getLastModifiedOn() {
		return lastModifiedOn;
	}

	public void setLastModifiedOn(LocalDateTime lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}

	@Override
	public String toString() {
		return "SellerPrice [seller=" + seller + ", sellerMedName=" + sellerMedName + ", buyURL=" + buyURL + ", price="
				+ price + ", qty=" + qty + ", subQty=" + subQty + ", imageURL=" + imageURL + ", manufacturer="
				+ manufacturer + ", lastModifiedOn=" + lastModifiedOn + "]";
	}

}
