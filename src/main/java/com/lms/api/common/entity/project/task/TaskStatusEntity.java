package com.lms.api.common.entity.project.task;

import com.lms.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;


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

    String projectFunctionId;
    String projectId;

    @OneToMany(mappedBy = "taskStatusEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    List<TaskEntity> taskEntities = new ArrayList<>();

}
