package com.lms.api.admin.project.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ProjectRole  {
  OWNER("소유자"),
  MEMBER ("팀원"),
  ;

  String label;
}
