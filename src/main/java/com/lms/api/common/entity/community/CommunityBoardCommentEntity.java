package com.lms.api.common.entity.community;

import com.lms.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "community_board_comment")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommunityBoardCommentEntity extends BaseEntity {

  @Id
  @Column(updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  String comment;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "community_board_id", nullable = false)
  @ToString.Exclude // 순환 참조 방지
  CommunityBoardEntity communityBoardEntity;

  @ToString.Exclude
  @OneToMany(mappedBy = "communityBoardCommentEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  List<CommunityBoardReplyEntity> communityBoardReplyEntities = new ArrayList<>();


}
