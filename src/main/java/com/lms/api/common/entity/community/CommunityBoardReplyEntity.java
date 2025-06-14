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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "community_board_comment_id", nullable = false)
  CommunityBoardCommentEntity communityBoardCommentEntity;


}
