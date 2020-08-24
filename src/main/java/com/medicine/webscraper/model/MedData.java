package com.medicine.webscraper.model;

import static com.medicine.webscraper.utils.Constants.SPACE;
import static com.medicine.webscraper.utils.Constants.EMPTY_STRING;


import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class MedData {

	private String id;
	private String medName;
	private String medNameAutoComplete;
	private String medNameToken0;
	private String medNameToken1;

	public MedData() {
		super();
	}

	public MedData(String medName) {
		this.id = (UUID.randomUUID().toString());
		this.medName = medName;
		this.medNameAutoComplete = medName;

		String tokens[] = medName.split(SPACE);
		if (tokens.length >= 2) {
			medNameToken0 = tokens[0];
			medNameToken1 = tokens[1];
		} else if (tokens.length >= 1) {
			medNameToken0 = tokens[0];
			medNameToken1 = EMPTY_STRING;
		} else {
			medNameToken0 = EMPTY_STRING;
			medNameToken1 = EMPTY_STRING;			
		}

	}

	public MedData(String id, String medName) {
		super();
		this.id = id;
		this.medName = medName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMedName() {
		return medName;
	}

	public void setMedName(String medName) {
		this.medName = medName;
	}

	public String getMedNameAutoComplete() {
		return medNameAutoComplete;
	}

	public void setMedNameAutoComplete(String medNameAutoComplete) {
		this.medNameAutoComplete = medNameAutoComplete;
	}

	public String getMedNameToken0() {
		return medNameToken0;
	}

	public void setMedNameToken0(String medNameToken0) {
		this.medNameToken0 = medNameToken0;
	}

	public String getMedNameToken1() {
		return medNameToken1;
	}

	public void setMedNameToken1(String medNameToken1) {
		this.medNameToken1 = medNameToken1;
	}

}
