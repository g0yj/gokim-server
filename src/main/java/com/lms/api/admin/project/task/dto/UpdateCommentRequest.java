package com.lms.api.admin.project.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter@Getter@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "UpdateCommentRequest", description = "댓글 수정 데이터")
public class UpdateCommentRequest {

    @Schema(description = "댓글 내용", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String content;
}
