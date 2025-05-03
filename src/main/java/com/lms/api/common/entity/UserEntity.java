package com.lms.api.common.entity;

import com.lms.api.common.dto.LoginType;
import com.lms.api.common.dto.UserRole;
import com.lms.api.common.entity.project.ProjectEntity;
import com.lms.api.common.entity.project.ProjectMemberEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

  @Enumerated(EnumType.STRING)
  UserRole role;
  @Enumerated(EnumType.STRING)
  LoginType loginType;

  @ToString.Exclude
  @Builder.Default
  @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  List<ProjectEntity> projectEntities = new ArrayList<>();

  @ToString.Exclude
  @Builder.Default
  @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  List<ProjectMemberEntity> projectMemberEntities = new ArrayList<>();

}
