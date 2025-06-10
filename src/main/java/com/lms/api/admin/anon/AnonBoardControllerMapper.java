package com.lms.api.admin.anon;


import com.lms.api.admin.anon.dto.*;
import com.lms.api.common.mapper.ControllerMapper;
import com.lms.api.common.mapper.ControllerMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;




@Mapper(componentModel = "spring", config = ControllerMapperConfig.class, uses = {
    ControllerMapper.class})
public interface AnonBoardControllerMapper {


    @Mapping(target = "multipartFiles", source ="createAnonBoardRequest.files")
    @Mapping(target = "createdBy", source = "loginId")
    CreateAnonBoard toCreateAnonBoard(String loginId, CreateAnonBoardRequest createAnonBoardRequest);
}
