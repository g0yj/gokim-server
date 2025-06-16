package com.lms.api.admin.board.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter@Setter@AllArgsConstructor@NoArgsConstructor
@Schema(name = "UpdateCommunityBoard", description = "게시글 수정 데이터")
public class UpdateCommunityBoard {
    @Schema(description = "제목", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String title;

    @Schema(description = "내용")
    String content;

    @Schema(description = "새롭게 추가된 파일")
    List<MultipartFile> files;

    @Schema(description = "상단 고정 여부")
    boolean pinned;

    @Schema(description = "삭제할 파일 식별키")
    List<Long> deleteFileIds;
}
