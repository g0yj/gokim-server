package com.lms.api.common.entity.project.task;

import com.lms.api.common.entity.*;
import com.lms.api.common.entity.project.ProjectEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name= "task")
@Getter@Setter @ToString(callSuper = true)
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    Long id;

    String title;

    @Column(columnDefinition = "TEXT")
    String content;

    @Column(nullable = false)
    Integer sortOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    ProjectEntity projectEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_status_id", nullable = false)
    TaskStatusEntity taskStatusEntity;

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
