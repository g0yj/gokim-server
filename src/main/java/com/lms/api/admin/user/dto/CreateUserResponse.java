package com.lms.api.admin.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter@Builder
@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "회원 가입 완료 시 반환 값. 가입 완료 시 관련 UI 필요할 때 사용 가능")
public class CreateUserResponse {

    @Schema(description = "아이디")
    String id;
    @Schema(description = "이름")
    String name;

}
