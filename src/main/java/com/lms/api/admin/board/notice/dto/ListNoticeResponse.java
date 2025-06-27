package com.lms.api.admin.board.notice.dto;

import com.lms.api.common.dto.PageResponseData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "ListNoticeResponse", description = "공지사항 목록입니다. 필터링 페이징 처리포함")
public class ListNoticeResponse extends PageResponseData {
    @Schema(description = "공지사항 식별키")
    String id;
    @Schema(description = "공지사항 제목")
    String title;
    @Schema(description = "공지사항 등록일")
    String createdOn;
    @Schema(description = "작성자 식별키")
    String createdBy;
    @Schema(description = "조회수")
    int view;
}
