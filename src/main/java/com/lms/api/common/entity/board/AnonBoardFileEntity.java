package com.lms.api.common.entity.board;


import com.lms.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "anon_board_file")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnonBoardFileEntity extends BaseEntity {

  @Id
  @Column(updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  String fileName;
  String originalFileName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "anon_board_id", nullable = false)
  @ToString.Exclude // 순환 참조 방지
  AnonBoardEntity anonBoardEntity;

}
