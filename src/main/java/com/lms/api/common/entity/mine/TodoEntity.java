package com.lms.api.common.entity.mine;

import com.lms.api.admin.auth.enums.LoginType;
import com.lms.api.admin.mine.enums.TodoStatus;
import com.lms.api.admin.user.enums.UserRole;
import com.lms.api.common.entity.BaseEntity;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.entity.project.ProjectEntity;
import com.lms.api.common.entity.project.ProjectMemberEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "todo")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TodoEntity extends BaseEntity {

  @Id
  @Column(updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  String title;

  @Enumerated(EnumType.STRING)
  TodoStatus todoStatus;

  int sort;

  String memo;
  LocalDate startDate;
  LocalDate endDate;
  String color;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  UserEntity userEntity;

}
