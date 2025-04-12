package com.lms.api.common.entity.project;

import com.lms.api.common.dto.Role;
import com.lms.api.common.entity.BaseEntity;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.entity.id.ProjectMemberId;
import com.lms.api.common.entity.project.ProjectEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Entity
@Setter@Getter@ToString
@Table(name = "project_member")
@IdClass(ProjectMemberId.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectMemberEntity extends BaseEntity {

    @Id
    @Column(name = "project_member_id", updatable = false)
    String projectMemberId;

    @Id
    @Column(name = "project_id", updatable = false)
    Long projectId;

    @Enumerated(EnumType.STRING)
    Role role;

    @MapsId("projectMemberId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_member_id" , insertable = false, updatable = false)
    UserEntity userEntity;

    @MapsId("projectId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    ProjectEntity projectEntity;
}
