package com.lms.api.common.entity.project.task;

import com.lms.api.common.entity.BaseEntity;
import com.lms.api.common.entity.project.ProjectEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Entity
@Setter@Getter
@ToString
@SuperBuilder@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "task_status")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskStatusEntity extends BaseEntity {

    @Id
    @Column(updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    ProjectEntity projectEntity;
}
