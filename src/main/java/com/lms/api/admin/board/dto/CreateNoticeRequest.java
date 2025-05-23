package com.lms.api.admin.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter@Setter@AllArgsConstructor@NoArgsConstructor
@Schema(name = "CreateNoticeRequest", description = "공지사항 등록 시 사용합니다")
public class CreateNoticeRequest {
    @Schema(description = "제목", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String title;
    @Schema(description = "본문")
    String content;
    @Schema(description = "상단 고정 여부" , requiredMode = Schema.RequiredMode.REQUIRED)
    boolean pinned;
    @Schema(description = "첨부파일")
    List<MultipartFile> files;

}
