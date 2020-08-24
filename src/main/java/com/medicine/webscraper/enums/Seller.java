package com.medicine.webscraper.enums;

import static com.medicine.webscraper.utils.Constants.HTTPS;

import java.util.Arrays;

import com.medicine.webscraper.utils.Commons;

public enum Seller {

	NETMEDS("netmeds.com", "www.netmeds.com", true),
	PHARMEASY("PharmEasy", "pharmeasy.in", true),
	ONEMG("1mg.com","www.1mg.com", true),
	PRACTO("practo", "www.practo.com", false),
	MEDPLUSMART("medplusmart", "www.medplusmart.com", true);

	private String name;
	private String domain;
	private boolean enabled;

	Seller(String name, String domain, boolean enabled) {
		this.name = name;
		this.domain = domain;
		this.enabled = enabled;
	}

	public String getName() {
		return name;
	}

	public String getDomain() {
		return domain;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public static boolean isSellerURL(String url) {
		if(url.length() < HTTPS.length()) {
			return false;
		}
		
		String incomingDomain = Commons.getDomainFromURL(url);
		long match = Arrays.asList(Seller.values())
						.stream()
						.filter(seller -> seller.domain.equals(incomingDomain) && seller.enabled)
						.count();
		
		return match == 1;
	}
	

}
