package com.lms.api.common.entity.community;

import com.lms.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "community_board")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommunityBoardEntity extends BaseEntity {

  @Id
  @Column(updatable = false)
  String id;

  String title;
  @Column(columnDefinition = "TEXT")
  String content;

  int view;

  boolean pinned;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "community_id", nullable = false)
  @ToString.Exclude // 순환 참조 방지
  CommunityEntity communityEntity;

  @ToString.Exclude
  @OneToMany(mappedBy = "communityBoardEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  List<CommunityBoardFileEntity> communityBoardFileEntities = new ArrayList<>();

  @ToString.Exclude
  @OneToMany(mappedBy = "communityBoardEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  List<CommunityBoardCommentEntity> communityBoardCommentEntities = new ArrayList<>();

  public void addFile(CommunityBoardFileEntity file) {
    communityBoardFileEntities.add(file);
    file.setCommunityBoardEntity(this);
  }

}
