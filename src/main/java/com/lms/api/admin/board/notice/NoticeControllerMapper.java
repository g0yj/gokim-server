package com.lms.api.admin.board.notice;


import com.lms.api.admin.board.dto.CreateNotice;
import com.lms.api.admin.board.dto.CreateNoticeRequest;
import com.lms.api.common.mapper.ControllerMapper;
import com.lms.api.common.mapper.ControllerMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", config = ControllerMapperConfig.class, uses = {
    ControllerMapper.class})
public interface NoticeControllerMapper {

    @Mapping(target = "multipartFiles", source ="createNoticeRequest.files")
    @Mapping(target = "createdBy", source = "loginId")
    CreateNotice toCreateNotice(String loginId, CreateNoticeRequest createNoticeRequest);
}
