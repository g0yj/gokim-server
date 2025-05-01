package com.lms.api.admin.service.dto;

import com.lms.api.common.dto.LoginType;
import com.lms.api.common.dto.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter@Getter
@NoArgsConstructor@AllArgsConstructor@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "로그인 응답")
public class LoginResponse {

    @Schema(description = "유저가 사용할 페이지와 관리자 페이지를 분기 처리하기 위해 사용")
    UserRole role;

    @Schema(description = "어떤 방식으로 로그인 했는가. 로그아웃 시에 분기 처리를 위해 필요한 값")
    LoginType loginType;

    @Schema(description = "JWT 토큰으로 로그인 api 호출 시 사용")
    String accessToken;
    @Schema(description = "JWT 토큰으로 일정 시간이 지났을 때 토큰을 재발급 받을 수 있도록 하는 api 호출 시 사용")
    String refreshToken;
}
