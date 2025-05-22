package com.lms.api.common.entity.project;

import com.lms.api.admin.project.enums.ProjectFunctionType;
import com.lms.api.common.entity.BaseEntity;
import com.lms.api.common.entity.project.file.ProjectFileEntity;
import com.lms.api.common.entity.project.task.TaskEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name= "project_function")
@Getter@Setter@SuperBuilder@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectFunctionEntity extends BaseEntity {

    @Id
    @Column(updatable = false)
    String id;

    String projectFunctionName;

    int projectFunctionSort;

    @Enumerated(EnumType.STRING)
    ProjectFunctionType projectFunctionType;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    ProjectEntity projectEntity;

    @ToString.Exclude
    @OneToMany(mappedBy = "projectFunctionEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    List<TaskEntity> taskEntities = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "projectFunctionEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ProjectFileEntity> projectFileEntities = new ArrayList<>();
}
