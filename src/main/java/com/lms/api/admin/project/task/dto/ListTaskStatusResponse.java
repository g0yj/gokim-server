package com.lms.api.admin.project.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter@Getter@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "ListTaskStatusResponse", description = "진행중, 완료 같은 상태")
public class ListTaskStatusResponse {
    @Schema(description = "상태 식별키")
    Long taskStatusId;
    @Schema(description = "상태명")
    String name;
}
