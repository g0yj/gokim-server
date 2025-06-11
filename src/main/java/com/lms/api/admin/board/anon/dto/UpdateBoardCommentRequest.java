package com.lms.api.admin.board.anon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor@NoArgsConstructor
@Schema(name = "UpdateBoardCommentRequest", description = "수정 댓글 데이터")
public class UpdateBoardCommentRequest {
    @Schema(description = "수정내용")
    @NotBlank
    String comment;
}
