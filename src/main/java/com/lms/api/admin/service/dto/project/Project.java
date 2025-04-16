package com.lms.api.admin.service.dto.project;

import com.lms.api.admin.service.dto.User;
import com.lms.api.admin.service.dto.project.task.Task;
import com.lms.api.common.dto.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Getter@Setter
@AllArgsConstructor@NoArgsConstructor
public class Project {
    String id;
    String projectName;
    String ownerName;

    List<ProjectMember> projectMembers;

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ProjectMember {
        String projectMemberId;
        String projectMemberName;
        Role role;
    }

}
