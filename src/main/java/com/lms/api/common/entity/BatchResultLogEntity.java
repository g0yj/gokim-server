package com.lms.api.common.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "batch_result_log")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BatchResultLogEntity extends BaseEntity {

  @Id
  @Column(updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  String jobName;
  LocalDateTime startedAt; // 배치 Job이 실제로 실행되기 시작한 시점
  LocalDateTime endedAt; // 배치 Job이 실제로 실행되기 종료 시점
  String status;
  String message;
  String note;
}
