package com.lms.api.admin.board.notice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter@Setter@Builder
@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "NoticeFile" , description = "공지사항 파일 정보")
public class NoticeFile {
    @Schema(description = "공지사항 파일 식별키")
    Long noticeFileId;
    @Schema(description = "파일명")
    String originalFileName;
    @Schema(description = "파일 경로")
    String url;
}
