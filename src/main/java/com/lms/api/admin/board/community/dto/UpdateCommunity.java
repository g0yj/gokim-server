package com.lms.api.admin.board.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter@Setter@AllArgsConstructor@NoArgsConstructor
@Schema(name = "UpdateCommunity", description = "수정 데이터")
public class UpdateCommunity {
    @Schema(description = "커뮤니티명", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String title;
    @Schema(description = "커뮤니티 설명")
    String description;
    @Schema(description = "커버 이미지")
    MultipartFile file;
}
