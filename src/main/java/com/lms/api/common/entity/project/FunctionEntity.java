package com.lms.api.common.entity.project;

import com.lms.api.common.entity.BaseEntity;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.entity.project.task.TaskEntity;
import com.lms.api.common.entity.project.task.TaskStatusEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name= "project_function")
@Getter@Setter@SuperBuilder@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FunctionEntity extends BaseEntity {

    @Id
    @Column(updatable = false)
    String id;

    String functionName;

    Long functionSort;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    ProjectEntity projectEntity;

    @ToString.Exclude
    @OneToMany(mappedBy = "functionEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    List<TaskEntity> taskEntities = new ArrayList<>();
}
