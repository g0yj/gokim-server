package com.lms.api.common.entity.project;

import com.lms.api.common.entity.BaseEntity;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.entity.project.task.TaskEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name= "project")
@Getter@Setter@SuperBuilder@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
