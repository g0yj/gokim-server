package com.lms.api.admin.board.notice.dto;

import com.lms.api.common.dto.PageResponse;
import org.springframework.data.domain.Page;

public class SwaggerListNotice extends PageResponse<ListNoticeResponse> {

    public SwaggerListNotice(Page<ListNoticeResponse> page, int pageSize) {
        super(page, pageSize);
    }
}
