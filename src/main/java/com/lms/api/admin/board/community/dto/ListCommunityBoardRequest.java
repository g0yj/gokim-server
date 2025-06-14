package com.lms.api.admin.board.community.dto;

import com.lms.api.common.dto.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "ListCommunityBoardRequest", description = "필터링 조건 데이터", allOf = {PageRequest.class})
public class ListCommunityBoardRequest extends PageRequest{
    @Schema(description = "검색 조건 (all: 전체, title: 제목, content:내용 , createdBy: 작성자")
    String search;

    public ListCommunityBoardRequest(Integer page, Integer limit, Integer pageSize, String order, String direction, String keyword, String search) {
        super(page, limit, pageSize, order, direction, keyword);
        this.search = search;
    }
}
