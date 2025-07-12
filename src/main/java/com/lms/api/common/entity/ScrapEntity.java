package com.lms.api.common.entity;

import com.lms.api.admin.auth.enums.LoginType;
import com.lms.api.admin.user.enums.UserRole;
import com.lms.api.common.dto.ScrapTableType;
import com.lms.api.common.entity.mine.TodoEntity;
import com.lms.api.common.entity.project.ProjectEntity;
import com.lms.api.common.entity.project.ProjectMemberEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "scrap")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScrapEntity extends BaseEntity {

  @Id
  @Column(updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, name = "table_type")
  ScrapTableType tableType;

  @Column(name = "target_id", nullable = false)
  String targetId;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  UserEntity userEntity;


}
