package com.lms.api.admin.controller;

import com.lms.api.common.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/v1")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

  private final LoginService loginService;
  private final LoginControllerMapper adminControllerMapper;


}
