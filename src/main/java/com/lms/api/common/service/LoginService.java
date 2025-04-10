package com.lms.api.common.service;

import com.lms.api.admin.service.dto.Login;
import com.lms.api.common.dto.LoginType;
import com.lms.api.common.dto.LoginInfo;
import com.lms.api.common.entity.UserLoginEntity;
import com.lms.api.common.exception.LmsErrorCode;
import com.lms.api.common.exception.LmsException;
import com.lms.api.common.repository.UserLoginRepository;
import com.lms.api.common.repository.UserRepository;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

  private final UserRepository userRepository;
  private final UserLoginRepository userLoginRepository;

}
