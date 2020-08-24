package com.medicine.webscraper.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.medicine.webscraper.model.MedData;

@JsonInclude(Include.NON_EMPTY)
public class SearchResponse extends BaseResponse {

	private List<MedData> medicines;

	public SearchResponse(String status, List<MedData> medicines) {
		super(status);
		this.medicines = medicines;
	}

	public SearchResponse(String status, String message) {
		super(status, message);
	}

	public List<MedData> getMedicines() {
		return medicines;
	}

	public void setMedicines(List<MedData> medicines) {
		this.medicines = medicines;
	}

}
