package com.lms.api.common.entity.project;

import com.lms.api.common.entity.BaseEntity;
import com.lms.api.common.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name= "project")
@Getter@Setter@SuperBuilder@NoArgsConstructor(access = AccessLevel.PUBLIC)
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
    List<ProjectMemberEntity> projectMemberEntities = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "projectEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ProjectFunctionEntity> projectFunctionEntities = new ArrayList<>();


}
