package com.medicine.webscraper.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(createIndex = true, indexName = "medicine_index")
public class Medicine {

	@Id
	private String id;
	private String name;
	private List<SellerPrice> priceList;

	public Medicine(String id, String name, List<SellerPrice> priceList) {
		super();
		this.id = id;
		this.name = name;
		this.priceList = priceList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<SellerPrice> getPriceList() {
		return priceList;
	}

	public void setPriceList(List<SellerPrice> priceList) {
		this.priceList = priceList;
	}

	@Override
	public String toString() {
		return "Medicine [id=" + id + ", name=" + name + ", priceList=" + priceList + "]";
	}

}
