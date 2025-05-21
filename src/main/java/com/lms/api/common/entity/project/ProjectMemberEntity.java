package com.lms.api.common.entity.project;

import com.lms.api.admin.project.enums.ProjectRole;
import com.lms.api.common.entity.BaseEntity;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.entity.id.ProjectMemberId;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Entity
@Setter@Getter@ToString@SuperBuilder@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "project_member")
@IdClass(ProjectMemberId.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectMemberEntity extends BaseEntity {

    @Id
    @Column(name = "project_member_id", updatable = false)
    String projectMemberId;

    @Id
    @Column(name = "project_id", updatable = false)
    String projectId;

    @Enumerated(EnumType.STRING)
    ProjectRole projectRole;

    @MapsId("projectId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    ProjectEntity projectEntity;

    @MapsId("projectMemberId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_member_id" , insertable = false, updatable = false)
    UserEntity userEntity;

}
