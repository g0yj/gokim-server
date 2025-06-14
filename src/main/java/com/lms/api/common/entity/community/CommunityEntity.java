package com.lms.api.common.entity.community;

import com.lms.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "community")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommunityEntity extends BaseEntity {

  @Id
  @Column(updatable = false)
  String id;

  String title;
  String description;

  // 커버 이미지 정보
  String fileName;
  String originalFileName;

  // 프로젝트화 여부
  Boolean hasProject;

  @ToString.Exclude
  @OneToMany(mappedBy = "communityEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  List<CommunityBoardEntity> communityBoardEntities = new ArrayList<>();

}
