package com.example.team1_be.utils.errors.exception;

import com.example.team1_be.utils.ApiUtils;
import com.example.team1_be.utils.errors.ClientErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ForbiddenException extends RuntimeException {
	private ClientErrorCode errorCode;

	public ForbiddenException(String message) {
		super(message);
		this.errorCode = ClientErrorCode.UNKNOWN_ERROR;
	}

	public ForbiddenException(String message, ClientErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public ApiUtils.ApiResult<?> body() {
		return ApiUtils.error(getMessage(), errorCode);
	}

	public HttpStatus status() {
		return HttpStatus.FORBIDDEN;
	}
}