package com.lms.api.admin.board.notice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter@NoArgsConstructor@AllArgsConstructor@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "UpdateNoticeRequest", description = "공지사항 수정할 데이터")
public class UpdateNoticeRequest {
    @Schema(description = "공지사항 제목")
    String title;
    @Schema(description = "공지사항 본문")
    String content;
    @Schema(description = "상단 고정 여부")
    boolean pinned;

    @Schema(description = "추가할 파일")
    List<MultipartFile> files;
    @Schema(description = "삭제할 파일의 식별키")
    List<Long> deleteFileIds;
}
