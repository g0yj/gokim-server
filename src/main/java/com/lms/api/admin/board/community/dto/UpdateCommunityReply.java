package com.lms.api.admin.board.community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name ="UpdateCommunityReply", description = "대댓 수정: 데이터")
public class UpdateCommunityReply {
    @Schema(description = "대댓", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String reply;
    @Schema(description = "대댓 공개 여부", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("isSecret")
    @NotNull
    Boolean isSecret;
}
