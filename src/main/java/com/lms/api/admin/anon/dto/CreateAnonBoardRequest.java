package com.lms.api.admin.anon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter@Setter@AllArgsConstructor@NoArgsConstructor
@Schema(name = "CreateAnonBoardRequest", description = "익명 게시글 등록에 사용")
public class CreateAnonBoardRequest {
    @Schema(description = "제목", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String title;
    @Schema(description = "본문")
    String content;
    @Schema(description = "첨부파일")
    List<MultipartFile> files;

}
