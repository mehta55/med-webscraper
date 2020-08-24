package com.medicine.webscraper.model.response;

public class BaseResponse {

	private String status;
	private String message;

	public BaseResponse(String status, String message) {
		super();
		this.status = status;
		this.message = message;
	}

	public BaseResponse(String status) {
		super();
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
