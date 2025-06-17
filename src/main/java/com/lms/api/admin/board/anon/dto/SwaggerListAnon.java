package com.lms.api.admin.board.anon.dto;

import com.lms.api.common.dto.PageResponse;
import org.springframework.data.domain.Page;

public class SwaggerListAnon extends PageResponse<ListAnonBoardResponse> {

    public SwaggerListAnon(Page<ListAnonBoardResponse> page, int pageSize) {
        super(page, pageSize);
    }
}
