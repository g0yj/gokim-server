package com.lms.api.common.entity.project.task;

import com.lms.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Setter@Getter
@ToString
@NoArgsConstructor
@Table(name = "task_status")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskStatusEntity extends BaseEntity {

    @Id
    @Column(updatable = false, nullable = false, unique = true, length = 50)
    String taskStatusId; // 예: NOT_STARTED, IN_PROGRESS, COMPLETED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    TaskEntity taskEntity;
}
