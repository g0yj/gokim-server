package com.lms.api.admin.project.dto;


import com.lms.api.admin.project.enums.ProjectRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Getter@Setter
@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "회원이 참여 중인 프로젝트 목록")
public class ListProjectResponse {
    @Schema(description = "프로젝트 식별키")
    String id;
    @Schema(description = "프로젝트 이름")
    String projectName;
    @Schema(description = "프로젝트 소유자 이름")
    String ownerName;
    @Schema(description = "프로젝트 소유자 식별키")
    String ownerId;

    List<ProjectMember> projectMembers;

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ProjectMember {
        @Schema(description = "프로젝트 참여 회원 식별키")
        String projectMemberId;
        @Schema(description = "프로젝트 참여 회원 이름")
        String projectMemberName;
        @Schema(description = "프로젝트 참여 회원 권한 : MEMBER-참여자 / OWNER- 소유자")
        ProjectRole projectRole;
    }

}
