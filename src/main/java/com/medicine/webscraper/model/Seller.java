package com.medicine.webscraper.model;

public class Seller {

	private String name;
	private String domain;
	private String logoURL;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getLogoURL() {
		return logoURL;
	}

	public void setLogoURL(String logoURL) {
		this.logoURL = logoURL;
	}

	@Override
	public String toString() {
		return "Seller [name=" + name + ", domain=" + domain + ", logoURL=" + logoURL + "]";
	}

	
}
