package com.lms.api.admin.board.community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter@Setter@AllArgsConstructor@NoArgsConstructor@Builder
@Schema(name ="UpdateCommunityComment", description = "댓글 수정 데이터")
public class UpdateCommunityComment {
    @Schema(description = "댓글", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String comment;

    @Schema(description = "댓글 공개 여부", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("isSecret")
    @NotNull
    Boolean isSecret;
}
