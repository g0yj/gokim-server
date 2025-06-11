package com.lms.api.admin.board.anon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor
@Schema(name = "CreateBoardCommentRequest", description = "익명 게시글에 대한 댓글 등록 데이터")
public class CreateBoardCommentRequest {
    @Schema(description = "댓글", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String comment;
}
