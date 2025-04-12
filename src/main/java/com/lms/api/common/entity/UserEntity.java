package com.lms.api.common.entity;

import com.lms.api.common.entity.project.ProjectEntity;
import com.lms.api.common.entity.project.ProjectMemberEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_")
@Getter
@Setter
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity extends BaseEntity {

  @Id
  @Column(updatable = false)
  String id;
  String password;
  String name;

  String email;
  String phone;

  String file;
  String originalFile;

  @ToString.Exclude
  @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  List<ProjectEntity> projectEntities = new ArrayList<>();

  @ToString.Exclude
  @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  List<ProjectMemberEntity> projectMemberEntities = new ArrayList<>();

}
