package com.lms.api.admin.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;
@Getter@Setter
@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "수정할 회원 데이터")
public class UpdateUserRequest {

    @Schema(description = "비밀번호")
    String password;

    @Schema(description = "이름")
    @NotBlank
    String name;

    @Schema(description = "전화번호")
    @NotBlank
    String phone;

    @Schema(description = "이메일")
    String email;

    @Schema(description = "프로필 이미지")
    MultipartFile file;

}
