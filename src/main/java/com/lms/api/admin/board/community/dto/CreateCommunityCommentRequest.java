package com.lms.api.admin.board.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter@Builder@AllArgsConstructor@NoArgsConstructor
@Schema(name = "CreateCommunityCommentRequest", description = "댓글 데이터")
public class CreateCommunityCommentRequest {
    @Schema(description = "댓글", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String comment;
}
