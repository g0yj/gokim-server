package com.lms.api.common.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum UserType {
  A("상담원"), // 상담원
  S("수강생"), // 수강생
  T("강사") // 강사
  ;

  String label;
}
