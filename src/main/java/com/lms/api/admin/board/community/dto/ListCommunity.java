package com.lms.api.admin.board.community.dto;

import com.lms.api.common.dto.PageResponseData;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListCommunity extends PageResponseData {

    String id;
    String url;
    String title;
    String description;
    String createdBy;
    Boolean isScrapped;

    String boardId;
}
