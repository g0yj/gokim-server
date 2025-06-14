package com.lms.api.admin.board.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor
@Schema(name ="CreateCommunityReply", description = "대댓 등록 데이터")
public class CreateCommunityReply {
    @Schema(description = "대댓", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String reply;
}
