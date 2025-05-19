package com.lms.api.common.entity.project.task;

import com.lms.api.common.entity.BaseEntity;
import com.lms.api.common.entity.project.ProjectMemberEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Entity
@Setter@Getter
@ToString
@Table(name = "sub_task")
@SuperBuilder@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubTaskEntity extends BaseEntity {

    @Id
    @Column(updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    TaskEntity taskEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_status_id", nullable = false)
    TaskStatusEntity taskStatusEntity;

    // 이 SubTask를 담당하는 팀원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "project_member_id", referencedColumnName = "project_member_id"),
            @JoinColumn(name = "project_id", referencedColumnName = "project_id")
    })
    ProjectMemberEntity assignee;

}
