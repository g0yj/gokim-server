package com.lms.api.common.entity.project.file;

import com.lms.api.common.entity.BaseEntity;
import com.lms.api.common.entity.project.ProjectFunctionEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name= "project_file")
@Getter@Setter@SuperBuilder@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectFileEntity extends BaseEntity {

    @Id
    @Column(updatable = false)
    String id;

    @Column(nullable = false)
    int sortOrder;

    @Column(nullable = false)
    String fileName;

    @Column(nullable = false)
    String originalFileName;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_function_id", nullable = false)
    ProjectFunctionEntity projectFunctionEntity;

}
