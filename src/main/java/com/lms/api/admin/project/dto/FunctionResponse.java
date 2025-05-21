package com.lms.api.admin.project.dto;

import com.lms.api.admin.project.enums.ProjectFunctionType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter@Setter@AllArgsConstructor@NoArgsConstructor@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FunctionResponse {

    ProjectFunctionType projectFunctionType;
    String functionName;

}
