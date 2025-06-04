package com.lms.api.admin.board.notice.dto;

import com.lms.api.common.dto.PageResponseData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter@Setter@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "ListPageNoticeResponse", description = "공지사항 목록입니다. 필터링 페이징 처리포함")
public class ListPageNoticeResponse  {

    @Schema(description = "공지사항 식별키")
    String id;
    @Schema(description = "공지사항 제목")
    String title;
    @Schema(description = "공지사항 최종 수정일")
    LocalDate createDate;
    @Schema(description = "작성자이름")
    String writerName;
    @Schema(description = "작성자 식별키")
    String writerId;
    @Schema(description = "첨부파일 갯수")
    int fileCount;
    @Schema(description = "조회수")
    int view;
    @Schema(description = "no")
    long listNumber;

}
