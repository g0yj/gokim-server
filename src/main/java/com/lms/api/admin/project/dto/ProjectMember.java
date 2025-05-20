package com.lms.api.admin.project.dto;

import com.lms.api.admin.project.enums.ProjectRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter@Setter@Builder
@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectMember {
    @Schema(description = "프로젝트 멤버 식별키")
    String id;
    @Schema(description = "프로젝트 멤버 이름")
    String name;
    @Schema(description = "프로젝트 멤버 이메일")
    String email;
    @Schema(description = "멤버 프로필 이미지")
    String userImgUrl;
    @Schema(description = "프로젝트 권한")
    ProjectRole projectRole;
}
