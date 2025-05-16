package com.lms.api.admin.auth.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum LoginType {
  NORMAL("일반"),
  KAKAO("카카오"),
  NAVER("네이버"),
  GOOGLE("구글")
  ;

  String label;
}
