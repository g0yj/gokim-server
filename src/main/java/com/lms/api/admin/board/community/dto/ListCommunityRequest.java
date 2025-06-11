package com.lms.api.admin.board.community.dto;

import com.lms.api.common.dto.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "ListCommunityRequest", description = "페이징 처리, 필터링 한 목록 조회", allOf = {PageRequest.class})
public class ListCommunityRequest extends PageRequest {

    @Schema(description = "커뮤니티  검색 조건 ( all, title, description )")
    String search;

    public ListCommunityRequest(Integer page, Integer limit, Integer pageSize, String order, String direction, String search, String keyword) {
        super(page, limit, pageSize, order, direction, keyword);
        this.search = search;

    }
}
