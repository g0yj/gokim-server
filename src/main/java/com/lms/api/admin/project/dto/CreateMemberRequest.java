package com.lms.api.admin.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter@Setter
@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "CreateMemberRequest", description = "프로젝트가 가진 기능 목록")
public class CreateMemberRequest {
    @Schema(description = "초대할 회원의 식별키", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String id;
}
