package com.hedvig.test.contract.premiuminsurance.exception;

import java.util.List;

import org.springframework.http.HttpStatus;

public class BusinessValidationException extends RuntimeException{
	public BusinessValidationException(String message) {
		this.message=message;
		this.httpStatus = HttpStatus.BAD_REQUEST;
	}
	public BusinessValidationException(List<String> messageList) {
		this.messageList=messageList;
		this.httpStatus = HttpStatus.BAD_REQUEST;
	}
	
	
	public BusinessValidationException(HttpStatus httpStatus, String message) {
		super();
		this.httpStatus = httpStatus;
		this.message = message;
	}

	
	public BusinessValidationException(HttpStatus httpStatus, List<String> messageList) {
		super();
		this.httpStatus = httpStatus;
		this.messageList = messageList;
	}


	private static final long serialVersionUID = 1L;
	private HttpStatus httpStatus;
	private String message;
	private List<String> messageList;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<String> getMessageList() {
		return messageList;
	}
	public void setMessageList(List<String> messageList) {
		this.messageList = messageList;
	}
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}
}
