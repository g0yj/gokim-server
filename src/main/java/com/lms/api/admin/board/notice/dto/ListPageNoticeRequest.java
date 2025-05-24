package com.lms.api.admin.board.notice.dto;

import com.lms.api.common.dto.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "ListPageNoticeRequest", description = "필터링 조건을 데이터 ", requiredMode = Schema.RequiredMode.REQUIRED)
public class ListPageNoticeRequest extends PageRequest {

    @Override
    @Schema(description = "공지 사항 검색 조건 ( all : 전체 , writerId : 작성자 식별키, title : 제목)")
    public String getSearch() {
        return super.getSearch();
    }
    public ListPageNoticeRequest(Integer page, Integer limit, Integer pageSize, String order, String direction, String search, String keyword) {
        super(page, limit, pageSize, order, direction, search, keyword);
    }

}
