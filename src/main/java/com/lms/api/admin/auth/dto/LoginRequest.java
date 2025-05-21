package com.lms.api.admin.auth.dto;

import com.lms.api.admin.auth.enums.LoginType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter@Getter
@NoArgsConstructor@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "LoginRequest", description = "로그인 요청")
public class LoginRequest {
    @Schema(description = "아이디", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String id;
    @Schema(description = "비밀번호", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String password;
    @Schema(description = "로그인 타입(로그아웃 시 분기 처리를 위함)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    LoginType loginType;

}
