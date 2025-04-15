package com.lms.api.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseEntity {

  @Column(updatable = false)
  String createdBy;

  @Column(updatable = false)
  @CreatedDate
  LocalDateTime createdOn;

  String modifiedBy;

  @LastModifiedDate
  LocalDateTime modifiedOn;

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
    this.modifiedBy = createdBy;
  }
}
