package com.lms.api.admin.project.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter@Setter@AllArgsConstructor@NoArgsConstructor@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "CreateTaskStatusResponse", description = "생성된 상태 식별키")
public class CreateTaskStatusResponse {
    Long taskStatusId;
}
