package com.lms.api.admin.project;

import com.lms.api.admin.project.dto.Project;
import com.lms.api.admin.project.dto.ProjectFunction;
import com.lms.api.common.entity.project.FunctionEntity;
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
    @Mapping(target = "projectMembers", source = "projectMemberEntities")
    Project toProject(ProjectEntity projectEntity);

    List<Project.ProjectMember> toProjectMember(List<ProjectMemberEntity> entities);
    @Mapping(target = "projectMemberId", source = "userEntity.id")
    @Mapping(target = "projectMemberName", source = "userEntity.name")
    Project.ProjectMember toProjectMember(ProjectMemberEntity projectMemberEntity);

    ProjectFunction.Function toFunction(FunctionEntity functionEntity);
}
