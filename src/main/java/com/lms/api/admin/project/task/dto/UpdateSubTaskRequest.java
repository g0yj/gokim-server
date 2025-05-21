package com.lms.api.admin.project.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter@Getter
@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "UpdateSubTaskRequest", description = "하위 항목 수정 ")
public class UpdateSubTaskRequest {
    @Schema(description = "프로젝트 식별키", requiredMode = Schema.RequiredMode.REQUIRED)
    String projectId;
    @Schema(description = "요약")
    String content;
    @Schema(description = "하위 항목 처리 상태 식별키(필수)", requiredMode = Schema.RequiredMode.REQUIRED)
    long subTaskStatusId;
    @Schema(description = "하위 항목 담당자 식별키")
    String subTaskAssignedMemberId;
}
