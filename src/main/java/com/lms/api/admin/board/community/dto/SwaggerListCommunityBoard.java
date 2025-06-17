package com.lms.api.admin.board.community.dto;

import com.lms.api.admin.board.anon.dto.ListAnonBoardResponse;
import com.lms.api.common.dto.PageResponse;
import org.springframework.data.domain.Page;

public class SwaggerListCommunityBoard extends PageResponse<ListCommunityBoardResponse> {
    public SwaggerListCommunityBoard(Page<ListCommunityBoardResponse> page, int pageSize) {
        super(page, pageSize);
    }
}
