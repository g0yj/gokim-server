package com.lms.api.admin.service.project;

import com.lms.api.admin.service.dto.project.Project;
import com.lms.api.common.entity.project.ProjectEntity;
import com.lms.api.common.mapper.ServiceMapper;
import com.lms.api.common.mapper.ServiceMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring", config = ServiceMapperConfig.class, uses = {ServiceMapper.class})
public interface ProjectServiceMapper {

    @Mapping(target = "ownerName", source = "projectEntity.userEntity.name")
    Project toProject(ProjectEntity projectEntity);
}
