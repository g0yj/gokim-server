package com.lms.api.admin.project.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
public enum ProjectRole  {
  OWNER("소유자"),
  MEMBER ("팀원"),
  ;

  private final String label;

}
