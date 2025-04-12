package com.lms.api.common.entity.project;

import com.lms.api.common.entity.BaseEntity;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.entity.project.task.TaskEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name= "project")
@Getter@Setter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectEntity extends BaseEntity {

    @Id
    @Column(updatable = false)
    String id;

    String projectName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    UserEntity userEntity;

    @ToString.Exclude
    @OneToMany(mappedBy = "projectEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    List<TaskEntity> taskEntities = new ArrayList<>();
}
