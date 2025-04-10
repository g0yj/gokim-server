package com.lms.api.common.exception;

import org.springframework.http.HttpStatusCode;

public interface LmsError {

  HttpStatusCode getHttpStatusCode();

  String getCode();

  String getMessage();
}
