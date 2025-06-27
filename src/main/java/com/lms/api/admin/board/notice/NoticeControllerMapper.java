package com.lms.api.admin.board.notice;


import com.lms.api.admin.board.notice.dto.*;
import com.lms.api.common.dto.PageResponse;
import com.lms.api.common.mapper.ControllerMapper;
import com.lms.api.common.mapper.ControllerMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;


@Mapper(componentModel = "spring", config = ControllerMapperConfig.class, uses = {
    ControllerMapper.class})
public interface NoticeControllerMapper extends ControllerMapper {

    @Mapping(target = "multipartFiles", source ="createNoticeRequest.files")
    @Mapping(target = "createdBy", source = "loginId")
    CreateNotice toCreateNotice(String loginId, CreateNoticeRequest createNoticeRequest);


    SearchNotice toSearchNotice(String id, ListPageNoticeRequest listPageNoticeRequest);

    ListNoticeResponse toListNoticeResponse(ListPageNotice listPageNotice);

    default PageResponse<ListNoticeResponse> toListNoticeResponse(
            SearchNotice search,
            Page<ListPageNotice> page
    ) {
        return toPageResponse(page, this::toListNoticeResponse, search.getPageSize());
    }
}
