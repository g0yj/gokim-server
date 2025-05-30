package com.lms.api.admin.mine;

import com.lms.api.admin.project.dto.ListProjectResponse;
import com.lms.api.admin.project.dto.Project;
import com.lms.api.common.mapper.ControllerMapper;
import com.lms.api.common.mapper.ControllerMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", config = ControllerMapperConfig.class, uses = {
    ControllerMapper.class})
public interface TodoControllerMapper {


}
