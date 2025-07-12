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

  // 잘못된 생각으로 추가한 필드. 스크랩은 회원마다 관리해야하기 때문에 별도의 테이블로 관리해야함.
  Boolean isScrapped;


  @ToString.Exclude
  @OneToMany(mappedBy = "communityEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  List<CommunityBoardEntity> communityBoardEntities = new ArrayList<>();

}
