package com.lms.api.admin.project.dto;

import com.lms.api.admin.project.enums.ProjectRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Getter@Setter
@AllArgsConstructor@NoArgsConstructor
public class Project {
    String id;
    String projectName;
    String ownerName;
    String ownerId;

    List<ProjectMember> projectMembers;

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ProjectMember {
        String projectMemberId;
        String projectMemberName;
        ProjectRole projectRole;
    }

}
