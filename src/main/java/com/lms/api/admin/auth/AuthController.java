package com.lms.api.admin.auth;

import com.lms.api.admin.auth.dto.LoginRequest;
import com.lms.api.admin.auth.dto.LoginResponse;
import com.lms.api.admin.auth.dto.NewTokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final AuthService authService;
  private final AuthControllerMapper authControllerMapper;

  @PostMapping("/login")
  @Operation(summary = "로그인")
  public LoginResponse login(@RequestBody @Validated LoginRequest request){
    return authService.login(request);
  }


  @PostMapping("/refresh")
  @Operation(summary = "일정 시간이 지나면 로그인 시 새로운 토큰이 필요. 시간이 지날 때 새로운 토큰을 받기 위해 사용")
  public NewTokenResponse refresh(HttpServletRequest request){
    return authService.refresh(request);
  }

  @PostMapping("/logout")
  @Operation(summary = "로그아웃")
  public ResponseEntity<?> logout(HttpServletRequest request){
    authService.logout(request);
    return ResponseEntity.ok().build();
  }

}
