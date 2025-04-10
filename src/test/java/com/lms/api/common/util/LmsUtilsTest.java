package com.lms.api.common.util;

import static com.lms.api.common.util.LmsUtils.checkPassword;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class LmsUtilsTest {

  @Test
  void testCheckPasswordWithInvalidPassword() {
    String password = "1111";
    String hashedPassword = "$2a$10$bnxOzQZpdKqRcKfAkL2TxuogsqenWLAmYuQzurt1cKtMpBtJyvPD2";

    // 패스워드 확인
    boolean isPasswordMatch = checkPassword(password, hashedPassword);
    System.out.println("isPasswordMatch: " + isPasswordMatch);
    assertTrue(isPasswordMatch);
  }

}