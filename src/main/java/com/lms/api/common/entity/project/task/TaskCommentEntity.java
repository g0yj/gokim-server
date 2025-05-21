package com.lms.api.common.entity.project.task;

import com.lms.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Entity
@Setter@Getter
@ToString@SuperBuilder@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "task_comment")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskCommentEntity extends BaseEntity {

    @Id
    @Column(updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    TaskEntity taskEntity;

}
