package com.lms.api.common.entity.board;


import com.lms.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notice_file")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NoticeFileEntity extends BaseEntity {

  @Id
  @Column(updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  String fileName;
  String originalFileName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "notice_id", nullable = false)
  NoticeEntity noticeEntity;

}
