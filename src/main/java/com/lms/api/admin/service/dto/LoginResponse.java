package com.lms.api.admin.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter@Getter
@NoArgsConstructor@AllArgsConstructor@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "로그인 응답")
public class LoginResponse {

    @Schema(description = "JWT 토큰")
    String accessToken;
    @Schema(description = "JWT 토큰")
    String refreshToken;
}
