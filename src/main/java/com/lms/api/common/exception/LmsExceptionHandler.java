package com.lms.api.common.exception;

import static com.lms.api.common.exception.LmsErrorCode.FILE_SIZE_EXCEEDED;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class LmsExceptionHandler {

  @Value("${lms.file.max-file-size}")
  private String maxFileSizeStr;

  @ExceptionHandler({ServletRequestBindingException.class, BindException.class,
      HttpMediaTypeNotSupportedException.class, HttpMessageNotReadableException.class})
  public ResponseEntity<LmsErrorResponse> handleServletRequestBindingException(Exception e) {
    log.warn(e.getMessage(), e);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(LmsErrorResponse.builder()
            .code("admin-api-9900")
            .message(e.getMessage())
            .build());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<LmsErrorResponse> handleException(Exception e) {
    log.error(e.getMessage(), e);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(LmsErrorResponse.builder()
            .code("admin-api-9999")
            .message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .build());
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST) // 400 에러 반환
  public ResponseEntity<LmsErrorResponse> handleMaxSizeException(
      MaxUploadSizeExceededException ex) {
    long exceededFileSize =
        ex.getCause() instanceof org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException
            ?
            ((org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException) ex.getCause()).getActualSize()
            : -1;

    String errorMessage = String.format(FILE_SIZE_EXCEEDED.getMessage(), maxFileSizeStr,
        exceededFileSize);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(LmsErrorResponse.builder()
            .code(FILE_SIZE_EXCEEDED.getCode())
            .message(errorMessage)
            .build());
  }

  @ExceptionHandler(LmsException.class)
  public ResponseEntity<LmsErrorResponse> handleLmsException(LmsException e) {
    if (e.getHttpStatusCode().is4xxClientError()) {
      log.warn(e.getMessage(), e);
    } else {
      log.error(e.getMessage(), e);
    }

    return ResponseEntity.status(e.getHttpStatusCode())
        .body(LmsErrorResponse.builder()
            .code(e.getCode())
            .message(e.getMessage())
            .build());
  }
}
