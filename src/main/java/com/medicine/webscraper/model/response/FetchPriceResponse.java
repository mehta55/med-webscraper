package com.medicine.webscraper.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.medicine.webscraper.model.Medicine;

@JsonInclude(Include.NON_EMPTY)
public class FetchPriceResponse extends BaseResponse {

	private Medicine medicine;

	public FetchPriceResponse(String status, Medicine medicine) {
		super(status);
		this.medicine = medicine;
	}

	public FetchPriceResponse(String status, String message) {
		super(status, message);
	}

	public Medicine getMedicine() {
		return medicine;
	}

	public void setMedicine(Medicine medicine) {
		this.medicine = medicine;
	}

}
