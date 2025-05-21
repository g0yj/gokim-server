package com.lms.api.common.entity.project;

import com.lms.api.admin.project.enums.ProjectFunctionType;
import com.lms.api.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name= "add_function")
@Getter@Setter@SuperBuilder@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FunctionEntity extends BaseEntity {

    @Id
    @Column(updatable = false)
    @Enumerated(EnumType.STRING)
    ProjectFunctionType projectFunctionType;

    String functionName;


}
