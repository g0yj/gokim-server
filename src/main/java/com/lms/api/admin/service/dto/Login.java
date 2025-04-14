package com.lms.api.admin.service.dto;

import com.lms.api.common.dto.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Login {
  
  String loginId;
  String password;

  // return
  String id;
  String name;
  String token;
}
