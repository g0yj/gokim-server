package com.lms.api.admin.board.community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lms.api.common.dto.PageRequest;
import com.lms.api.common.dto.PageResponseData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "ListCommunityResponse", description = "필터링 조건을 데이터 ",  allOf = {PageRequest.class})
public class ListCommunityResponse extends PageResponseData {
    @Schema(description = "커뮤니티 식별키")
    String id;
    @Schema(description = "커버 이미지")
    String url;
    @Schema(description = "제목")
    String title;
    @Schema(description = "설명")
    String description;
    @Schema(description = "생성자")
    String createdBy;

    @Schema(description = "스크랩 여부")
    @JsonProperty("isScrapped")
    Boolean isScrapped;

    @Schema(description = "커뮤니티 게시판 식별키")
    String boardId;

}
