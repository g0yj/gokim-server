package com.lms.api.admin.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter@Getter
@NoArgsConstructor@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "로그인 요청")
public class LoginRequest {
    @Schema(description = "아이디", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String id;
    @Schema(description = "비밀번호", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String password;
}
