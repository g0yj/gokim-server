package com.lms.api.common.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ApiErrorCode implements ApiError {
  // common error
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "9999", "서버에 문제가 발생했습니다."),
  BAD_REQUEST(HttpStatus.BAD_REQUEST, "9900", "잘못된 요청입니다. {}"),
  INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "9901", "잘못된 파라미터입니다. ({})"),
  PARAMETER_REQUIRED(HttpStatus.BAD_REQUEST, "9901", "{} 파라미터는 필수입니다."),

  // auth error
  LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "0100", "로그인이 필요합니다."),
  PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "0101", "비밀번호가 다릅니다."),
  USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "0102", "사용자를 찾을 수 없습니다."),
  ACCESS_DENIED(HttpStatus.FORBIDDEN, "0105", "접근 권한이 없습니다."),
  ID_NOT_EXIST(HttpStatus.BAD_REQUEST, "0106", "없는 아이디입니다."),

  // loginId error
  LOGIN_SERVER_ERROR(HttpStatus.CONFLICT, "1400", "동일한 ID가 존재합니다"),

  // password error
  CHANGEPW1_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "1401", "현재 비밀번호가 일치하지 않습니다"),
  CHANGEPW2_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "1402", "아이디와 같은 비밀번호는 사용할 수 없습니다"),
  CHANGEPW3_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "1403", "비밀번호와 확인비밀번호가 일치하지 않습니다"),
  CHANGEPW4_SERVER_ERROR(
      HttpStatus.INTERNAL_SERVER_ERROR, "1404", "비밀번호는 6~14자리의 영문과 숫자를 포함해야 하며 공백을 포함할 수 없습니다."),

  //file error
  FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "1500", "파일 크기가 {}를 초과합니다: {}"),
  FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "1501", "{}"),

  //project error
  PROJECT_NOT_FOUND(HttpStatus.BAD_REQUEST, "0200", "찾을 수 없는 프로젝트입니다."),
  PROJECT_MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "0201", "프로젝트 참여 멤버가 아닙니다"),
  FUNCTION_NOT_FOUND(HttpStatus.BAD_REQUEST, "0202", "찾을 수 없는 프로젝트 기능입니다"),

  //task error
  TASK_STATUS_NOT_FOUND(HttpStatus.BAD_REQUEST,"0300", "찾을 수 없는 상태입니다."),
  TASK_NOT_FOUND(HttpStatus.BAD_REQUEST,"0301", "찾을 수 없는 task입니다."),
  SUB_TASK_NOT_FOUND(HttpStatus.BAD_REQUEST,"0302", "찾을 수 없는 하위 항목입니다.")

  ;
  HttpStatusCode httpStatusCode;
  String code;
  String message;

  public String getCode() {
    return "api-" + code;
  }
}
