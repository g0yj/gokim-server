package com.lms.api.admin.project.dto;

import com.lms.api.admin.project.enums.ProjectRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter@Setter
@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "UpdateMemberRequest", description = "권한을 변경할 때 사용")
public class UpdateMemberRequest {
    @Schema(description = "프로젝트 참여 멤버 식별키", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String projectMemberId;

    @Schema(description = "프로젝트 참여 멤버 식별키", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    ProjectRole projectRole;
}
