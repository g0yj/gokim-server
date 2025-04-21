package com.lms.api.common.entity.project.task;

import com.lms.api.common.entity.*;
import com.lms.api.common.entity.project.ProjectEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name= "task")
@Getter@Setter @ToString(callSuper = true)
@SuperBuilder@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskEntity extends BaseEntity {

    @Id
    @Column(updatable = false)
    String id;

    String title;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(nullable = false)
    int sortOrder;

    String assignedMember;
    Long taskStatusId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    ProjectEntity projectEntity;

    // 하위
    @ToString.Exclude
    @OneToMany(mappedBy = "taskEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    List<SubTaskEntity> subTaskEntities = new ArrayList<>();

    //파일
    @ToString.Exclude
    @OneToMany(mappedBy = "taskEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    List<TaskFileEntity> taskFileEntities = new ArrayList<>();

    //댓글
    @ToString.Exclude
    @OneToMany(mappedBy = "taskEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    List<TaskCommentEntity> taskCommentEntities = new ArrayList<>();

}
