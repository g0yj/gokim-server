package com.lms.api.admin.board.anon.dto;

import com.lms.api.common.dto.PageResponseData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter@Setter@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "ListAnonBoardResponse", description = "익명 게시판 목록입니다. 필터링 페이징 처리포함")
public class ListAnonBoardResponse extends PageResponseData  {

    @Schema(description = "게시글 식별키")
    String id;
    @Schema(description = "게시글 제목")
    String title;
    @Schema(description = "공지사항 최종 수정일")
    LocalDate createDate;
    @Schema(description = "첨부파일 갯수")
    int fileCount;
    @Schema(description = "조회수")
    int view;
    @Schema(description = "no")
    long listNumber;

}
