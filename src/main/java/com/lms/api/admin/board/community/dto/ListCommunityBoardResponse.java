package com.lms.api.admin.board.community.dto;

import com.lms.api.common.dto.PageResponseData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter@Setter@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "ListCommunityBoardResponse", description = "페이징 처리된 커뮤니티 게시판")
public class ListCommunityBoardResponse {
    @Schema(description = "게시판 식별키")
    String id;
    @Schema(description = "제목")
    String title;
    @Schema(description = "조회수")
    int view;
    @Schema(description = "작성일")
    String createdOn;
    @Schema(description = "작성자")
    String createdBy;
    @Schema(description = "댓글수")
    int commentCount;
    @Schema(description = "no")
    long listNumber;



}
