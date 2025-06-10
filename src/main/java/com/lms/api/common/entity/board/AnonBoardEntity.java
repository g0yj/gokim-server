package com.lms.api.common.entity.board;

import com.lms.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "anon_board")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnonBoardEntity extends BaseEntity {

  @Id
  @Column(updatable = false)
  String id;

  String title;

  @Column(columnDefinition = "TEXT")
  String content;

  Long view;

  @Builder.Default
  @ToString.Exclude
  @OneToMany(mappedBy = "anonBoardEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  List<AnonBoardFileEntity> anonBoardFileEntities = new ArrayList<>();

  public void addFile(AnonBoardFileEntity file) {
    this.anonBoardFileEntities.add(file);
    file.setAnonBoardEntity(this);
  }

}
