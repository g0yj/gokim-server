package com.lms.api.admin.board.anon.dto;

import com.lms.api.common.dto.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "ListAnonBoardRequest", description = "필터링 조건을 데이터 ", allOf = {PageRequest.class})
public class ListAnonBoardRequest extends PageRequest {
    @Schema(description = " 익명 게시판 검색 조건 ( all : 전체 , title : 제목, content : 내용)")
    String search;

    public ListAnonBoardRequest(Integer page, Integer limit, Integer pageSize, String order, String direction, String keyword) {
        super(page, limit, pageSize, order, direction, keyword);
    }
}
