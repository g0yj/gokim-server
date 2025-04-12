package com.lms.api.common.exception;

import org.springframework.http.HttpStatusCode;

public interface ApiError {

  HttpStatusCode getHttpStatusCode();

  String getCode();

  String getMessage();
}
