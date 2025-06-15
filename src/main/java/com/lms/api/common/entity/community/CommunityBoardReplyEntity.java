package com.lms.api.common.entity.community;

import com.lms.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "community_board_reply")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommunityBoardReplyEntity extends BaseEntity {

  @Id
  @Column(updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  String reply;

  boolean isSecret;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "community_board_comment_id", nullable = false)
  CommunityBoardCommentEntity communityBoardCommentEntity;

  // 스프링부트는 boolean일 때 getter 생성 시 isxxx로 만듦. 변수명이 이미 isSecret이기 때문에 @Getter로 제대로 생성이 안되서 별도로 만들어야함.
  public boolean getIsSecret() {
    return isSecret;
  }

}
