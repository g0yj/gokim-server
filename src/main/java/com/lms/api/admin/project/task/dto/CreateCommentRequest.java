package com.lms.api.admin.project.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter@Setter@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "CreateCommentRequest" , description = "댓글 등록 시 사용 데이터")
public class CreateCommentRequest {
    @Schema(description = "댓글 내용", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String content;
}
