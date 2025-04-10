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
public enum LmsErrorCode implements LmsError {
  // common error
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "9999", "서버에 문제가 발생했습니다."),
  BAD_REQUEST(HttpStatus.BAD_REQUEST, "9900", "잘못된 요청입니다. {}"),
  INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "9901", "잘못된 파라미터입니다. ({})"),
  PARAMETER_REQUIRED(HttpStatus.BAD_REQUEST, "9901", "{} 파라미터는 필수입니다."),

  // auth error
  LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "0100", "로그인이 필요합니다."),
  PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "0101", "비밀번호가 다릅니다."),
  USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "0102", "사용자를 찾을 수 없습니다."),
  USER_INACTIVE(HttpStatus.BAD_REQUEST, "0104", "비활성화 된 사용자입니다."),
  ACCESS_DENIED(HttpStatus.FORBIDDEN, "0105", "접근 권한이 없습니다."),
  ID_NOT_EXIST(HttpStatus.BAD_REQUEST, "0106", "없는 아이디입니다."),

  // user error
//    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "0200", "사용자를 찾을 수 없습니다."),

  // consultation error
  CONSULTATION_NOT_FOUND(HttpStatus.NOT_FOUND, "0300", "상담을 찾을 수 없습니다."),
  CONSULTATION_EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "0301", "이메일이 없습니다."),
  CONSULTATION_ALREADY_MEMBER(HttpStatus.BAD_REQUEST, "0302", "이미 회원입니다."),
  CONSULTATION_EMAIL_ALREADY_EXISTS(
      HttpStatus.BAD_REQUEST, "0303", "이메일은 회원의 아이디가 됩니다. 중복 방지를 위해 이메일을 변경한 뒤 회원 등록 해주세요"),
  CONSULTATION_DATE(HttpStatus.BAD_REQUEST, "0304", "상담일시는 필수입니다. 상담일시를 수정한 뒤 다시 등록해주세요."),

  // course error
  COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "0400", "강의를 찾을 수 없습니다."),
  LESSON_COUNT_NOT_ENOUGH(HttpStatus.BAD_REQUEST, "0401", "수업 횟수가 부족합니다."),
  RESERVATIONS_EXIST_OUTSIDE_COURSE_DATE(HttpStatus.BAD_REQUEST, "0402", "코스 기간 외 예약이 존재합니다."),

  // teacher error
  TEACHER_NOT_FOUND(HttpStatus.NOT_FOUND, "0500", "강사를 찾을 수 없습니다."),

  // reservation error
  RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "0600", "예약을 찾을 수 없습니다."),
  SCHEDULE_ALREADY_RESERVED(HttpStatus.BAD_REQUEST, "0601", "이미 예약된 스케줄입니다."),
  RESERVATION_NOT_CANCELABLE(
      HttpStatus.BAD_REQUEST, "0602", "취소할 수 없는 예약을 포함하고 있습니다. 취소는 2일 전(일요일, 공휴일 제외)까지 가능합니다"),
  SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "0603", "스케줄을 찾을 수 없습니다."),
  RESERVATION_DATE_INVALID(HttpStatus.BAD_REQUEST, "0604", "예약 날짜가 유효하지 않습니다."),
  SCHEDULE_DELETION_NOT_ALLOWED(HttpStatus.CONFLICT, "0605", "예약된 스케쥴({})을 삭제할 수 없습니다."),

  // order error
  ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "0700", "주문을 찾을 수 없습니다."),
  BILLING_AMOUNT_NOT_MATCH(HttpStatus.BAD_REQUEST, "0701", "금액이 일치하지 않습니다."),
  PAYMENT_AMOUNT_NOT_POSITIVE(HttpStatus.BAD_REQUEST, "0702", "결제 금액은 0보다 커야 합니다."),
  PAYMENT_AMOUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "0703", "결제 금액이 초과되었습니다."),
  RECEIVABLE_AMOUNT_NOT_MATCH(HttpStatus.BAD_REQUEST, "0704", "미수금액이 일치하지 않습니다."),
  PAYMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "0705", "결제정보를 찾을 수 없습니다."),
  PAYMENT_CANNOT_CANCEL(HttpStatus.BAD_REQUEST, "0706", "결제를 취소할 수 없습니다."),
  REFUND_AMOUNT_NOT_POSITIVE(HttpStatus.BAD_REQUEST, "0707", "환불 금액은 0보다 커야 합니다."),
  REFUND_AMOUNT_NOT_MATCH(HttpStatus.BAD_REQUEST, "0708", "환불 금액이 일치하지 않습니다."),
  PAYMENT_EXISTS(HttpStatus.BAD_REQUEST, "0709", "결제가 존재합니다."),
  RESERVATION_EXISTS(HttpStatus.BAD_REQUEST, "0710", "예약이 존재합니다."),
  REFUND_AMOUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "0711", "환불 금액을 초과하였습니다."),

  // product error
  PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "0800", "상품을 찾을 수 없습니다."),

  // ldf error
  LDF_NOT_FOUND(HttpStatus.NOT_FOUND, "0900", "Ldf를 찾을 수 없습니다"),

  // consultation error
//    CONSULTATION_NOT_FOUND(HttpStatus.NOT_FOUND,"1000","상담 내역을 찾을 수 없습니다"),

  // valCellPhone error
  CELLPHONE_NOT_MATCH(HttpStatus.BAD_REQUEST, "1001", "중복되는 번호가 있습니다"),

  // course_history error ( = note error)
  NOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "1100", "비고 내역을 찾을 수 없습니다"),

  // levelTest error
  LEVELTEST_NOT_FOUND(HttpStatus.NOT_FOUND, "1200", "레벨테스트를 찾을 수 없습니다"),

  // sms error
  SMS_NOT_FOUND(HttpStatus.NOT_FOUND, "1300", "SMS를 찾을 수 없습니다"),

  //template error
  TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "1400", "Template를 찾을 수 없습니다"),

  // commonCode error
  COMMONCODE_NOT_FOUND(HttpStatus.NOT_FOUND,"1500","찾을 수 없는 공통코드 입니다"),
  COMMONCODE_CONFLICT(HttpStatus.CONFLICT,"1501","중복코드가 있습니다."),

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

  //mail error
  EMAIL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "1600", "메일 전송이 실패했습니다."),

  //schedule
  SCHEDULE_CONFLICT(HttpStatus.BAD_REQUEST, "1700", "예약 시간이 기존 예약({} - {})과 겹칩니다."),
  SCHEDULE_OUTSIDE_COURSE_DATE_RANGE(HttpStatus.BAD_REQUEST, "1701", "예약할 강사 스케줄의 시작일과 끝일이 수업 일정을 벗어납니다.");

  HttpStatusCode httpStatusCode;
  String code;
  String message;

  public String getCode() {
    return "api-" + code;
  }
}
