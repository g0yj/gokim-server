package com.lms.api.admin.board.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "CreateCommunityBoardRequest" , description = "커뮤니티 게시글 등록 데이터")
public class CreateCommunityBoardRequest {
    @Schema(description = "제목", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String title;

    @Schema(description = "내용")
    String content;

    @Schema(description = "첨부 파일")
    List<MultipartFile> files;

    @Schema(description = "상단 고정 여부")
    boolean pinned;
}
