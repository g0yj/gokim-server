package com.lms.api.admin.controller.dto.project;


import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter@Setter
@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "프로젝트가 가진 기능 목록입니다. 식별키를 통해 기능 구분이 가능합니다. null인 경우 구현된 기능을 포함하는 프로젝트가 아닙니다")
public class GetProjectResponse {
    @Schema(description = "프로젝트 식별키")
    String projectId;

    @ArraySchema(
            schema = @Schema(description = "할일 식별키"),
            arraySchema = @Schema(description = "todo 관련 기능으로, '보드' 카테고리에 사용됩니다")
    )
    List<String> taskId;

}
