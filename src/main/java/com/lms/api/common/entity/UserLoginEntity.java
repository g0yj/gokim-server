package com.lms.api.common.entity;

import com.lms.api.common.dto.LoginType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "user_login")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLoginEntity extends BaseEntity {

  @Id
  @Column(updatable = false)
  String token;

  @Enumerated(EnumType.STRING)
  LoginType loginType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId", nullable = false)
  UserEntity userEntity;
}
