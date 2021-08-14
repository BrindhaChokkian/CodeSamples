package com.hedvig.test.contract.premiuminsurance.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class PremiumInsuranceExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler(BusinessValidationException.class)
	public final ResponseEntity<Object> handleBusinessValidationException(BusinessValidationException ex,
			WebRequest request) {
		List<String> details = new ArrayList<>();
		if (ex.getMessageList() != null) {
			for (String error : ex.getMessageList()) {
				details.add(error);
			}
		}
		if (ex.getMessage() != null) {
			details.add(ex.getMessage());
		}
		ApiError apiError = new ApiError(ex.getHttpStatus(), ex.getMessage(), details);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<String> details = new ArrayList<>();
		for (ObjectError error : ex.getBindingResult().getAllErrors()) {
			details.add(error.getDefaultMessage());
		}
		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), details);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
	}
}
