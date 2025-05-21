package com.lms.api.admin.project;

import com.lms.api.admin.project.dto.FunctionResponse;
import com.lms.api.admin.project.dto.Project;
import com.lms.api.admin.project.dto.ProjectFunction;
import com.lms.api.common.entity.project.FunctionEntity;
import com.lms.api.common.entity.project.ProjectFunctionEntity;
import com.lms.api.common.entity.project.ProjectEntity;
import com.lms.api.common.entity.project.ProjectMemberEntity;
import com.lms.api.common.mapper.ServiceMapper;
import com.lms.api.common.mapper.ServiceMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring", config = ServiceMapperConfig.class, uses = {ServiceMapper.class})
public interface ProjectServiceMapper {
    @Mapping(target = "ownerName", source = "projectEntity.userEntity.name")
    @Mapping(target = "ownerId", source = "projectEntity.userEntity.id")
    @Mapping(target = "projectMembers", source = "projectMemberEntities")
    Project toProject(ProjectEntity projectEntity);

    List<Project.ProjectMember> toProjectMember(List<ProjectMemberEntity> entities);
    @Mapping(target = "projectMemberId", source = "userEntity.id")
    @Mapping(target = "projectMemberName", source = "userEntity.name")
    Project.ProjectMember toProjectMember(ProjectMemberEntity projectMemberEntity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "projectFunctionName", source = "projectFunctionName")
    @Mapping(target = "projectFunctionSort", source = "projectFunctionSort")
    @Mapping(target = "projectFunctionType", source = "projectFunctionType")
    ProjectFunction.Function toFunction(ProjectFunctionEntity projectFunctionEntity);

    FunctionResponse toFunctionResponse(FunctionEntity functionEntity);
}
