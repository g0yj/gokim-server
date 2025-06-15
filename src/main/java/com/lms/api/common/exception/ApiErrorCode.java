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
  FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "1500", "파일 크기가 %s를 초과합니다:  %s"),
  FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "1501", "파일 업로드 실패"),
  UNSUPPORTED_FORMAT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "1502", "지원하지 않은 확장자입니다"),
  TOO_MANY_FILES(HttpStatus.INTERNAL_SERVER_ERROR, "1503", "하나의 파일만 사용해주세요"),

  //project error
  PROJECT_NOT_FOUND(HttpStatus.BAD_REQUEST, "0200", "찾을 수 없는 프로젝트입니다."),
  PROJECT_MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "0201", "프로젝트 참여 멤버가 아닙니다"),
  FUNCTION_NOT_FOUND(HttpStatus.BAD_REQUEST, "0202", "찾을 수 없는 기능입니다"),
  PROJECT_FUNCTION_NOT_FOUND(HttpStatus.BAD_REQUEST, "0203", "찾을 수 없는 프로젝트 기능입니다"),
  PROJECT_FILE_NOT_FOUND(HttpStatus.BAD_REQUEST, "0204", "찾을 수 없는 프로젝트 파일입니다"),

  //task error
  TASK_STATUS_NOT_FOUND(HttpStatus.BAD_REQUEST,"0300", "찾을 수 없는 상태입니다."),
  TASK_NOT_FOUND(HttpStatus.BAD_REQUEST,"0301", "찾을 수 없는 task입니다."),
  SUB_TASK_NOT_FOUND(HttpStatus.BAD_REQUEST,"0302", "찾을 수 없는 하위 항목입니다."),
  TASK_COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST,"0303", "찾을 수 없는 댓글 입니다"),

  //notice error
  NOTICE_NOT_FOUND(HttpStatus.BAD_REQUEST,"0400", "찾을 수 없는 공지사항 입니다."),

  // tod o error
  TODO_NOT_FOUND(HttpStatus.BAD_REQUEST, "0500", "찾을 수 없는 TODO 입니다."),

  // anonBoard error
  ANONBOARD_NOT_FOUND(HttpStatus.BAD_REQUEST, "0600", "찾을 수 없는 익명 게시글 입니다."),
  ANONBOARD_COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "0601", "찾을 수 없는 댓글 입니다"),

  // community error
  COMMUNITY_NOT_FOUND(HttpStatus.BAD_REQUEST, "0700", "찾을 수 없는 커뮤니티입니다"),
  COMMUNITY_COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "0701", "찾을 수 없는 커뮤니티 댓글입니다"),
  COMMUNITY_BOARD_NOT_FOUND(HttpStatus.BAD_REQUEST, "0702", "찾을 수 없는 커뮤니티 게시글입니다"),
  COMMUNITY_REPLY_NOT_FOUND(HttpStatus.BAD_REQUEST, "0703", "찾을 수 없는 대댓입니다"),

  ;
  HttpStatusCode httpStatusCode;
  String code;
  String message;

  public String getCode() {
    return "api-" + code;
  }
}
