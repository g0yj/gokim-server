package com.lms.api.common.service;

import com.lms.api.common.repository.UserLoginRepository;
import com.lms.api.common.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

  private final UserRepository userRepository;
  private final UserLoginRepository userLoginRepository;

}
