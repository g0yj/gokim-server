package com.lms.api.common.entity.board;

import com.lms.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notice")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NoticeEntity extends BaseEntity {

  @Id
  @Column(updatable = false)
  String id;

  String title;

  @Column(columnDefinition = "TEXT")
  String content;

  boolean pinned;
  int view;


  @ToString.Exclude
  @OneToMany(mappedBy = "noticeEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  List<NoticeFileEntity> noticeFileEntities = new ArrayList<>();

}
