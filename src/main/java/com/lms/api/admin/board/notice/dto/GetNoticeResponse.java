package com.lms.api.admin.board.notice.dto;

import com.lms.api.admin.user.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter@Setter@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "GetNoticeResponse", description = "공지사항 상세조회")
public class GetNoticeResponse {
    @Schema(description = "공지사항 식별키")
    String id;
    @Schema(description = "제목")
    String title;
    @Schema(description = "본문")
    String content;
    @Schema(description = "조회수")
    int view;
    @Schema(description = "상단 고정 여부 노출 여부와 수정, 삭제 버튼을 위해 사용")
    UserRole userRole;
    @Schema(description = "상단 고정 여부")
    boolean pinned;
    @Schema(description = "파일 식별키")
    List<NoticeFile> files;



}
