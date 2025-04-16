package com.lms.api.admin.controller.project;

import com.lms.api.admin.controller.dto.project.GetProjectResponse;
import com.lms.api.admin.controller.dto.project.ListProjectResponse;
import com.lms.api.admin.service.dto.project.Function;
import com.lms.api.admin.service.dto.project.Project;
import com.lms.api.common.mapper.ControllerMapper;
import com.lms.api.common.mapper.ControllerMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", config = ControllerMapperConfig.class, uses = {
    ControllerMapper.class})
public interface ProjectControllerMapper {

    List<ListProjectResponse> toListProjectResponse(List<Project> projects);
    @Mapping(target = "projectMembers" , source = "project.projectMembers")
    ListProjectResponse toListProjectResponse(Project project);

    @Mapping(target = "projectId", source = "projectId")
    GetProjectResponse toGetProjectResponse(Function project);

}
