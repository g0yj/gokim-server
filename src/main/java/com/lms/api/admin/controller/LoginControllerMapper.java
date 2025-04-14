package com.lms.api.admin.controller;

import com.lms.api.admin.service.dto.Login;
import com.lms.api.common.controller.dto.LoginRequest;
import com.lms.api.common.controller.dto.LoginResponse;
import com.lms.api.common.mapper.ControllerMapper;
import com.lms.api.common.mapper.ControllerMapperConfig;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", config = ControllerMapperConfig.class, uses = {
    ControllerMapper.class})
public interface LoginControllerMapper {


  @Mapping(target = "loginId", source = "id")
  Login toLogin(LoginRequest loginRequest);

  LoginResponse toLoginResponse(Login login);

}
