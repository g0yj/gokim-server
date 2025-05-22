package com.lms.api.admin.project.file;

import com.lms.api.admin.project.file.dto.CreateProjectFileRequest;
import com.lms.api.admin.project.file.dto.CreateProjectFile;
import com.lms.api.common.mapper.ControllerMapper;
import com.lms.api.common.mapper.ControllerMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", config = ControllerMapperConfig.class, uses = {
    ControllerMapper.class})
public interface ProjectFileControllerMapper {
    @Mapping(target = "multipartFiles" , source = "createProjectFileRequest.files")
    @Mapping(target = "createdBy" , source = "loginId")
    @Mapping(target = "projectFunctionId" , source = "projectFunctionId")
    CreateProjectFile toProjectFile(String loginId, String projectFunctionId, CreateProjectFileRequest createProjectFileRequest);
}
