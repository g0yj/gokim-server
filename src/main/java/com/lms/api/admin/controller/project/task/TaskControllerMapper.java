package com.lms.api.admin.controller.project.task;

import com.lms.api.admin.controller.dto.project.task.ListTaskResponse;
import com.lms.api.admin.service.dto.project.task.ListTask;
import com.lms.api.common.mapper.ControllerMapper;
import com.lms.api.common.mapper.ControllerMapperConfig;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", config = ControllerMapperConfig.class, uses = {
    ControllerMapper.class})
public interface TaskControllerMapper {

    List<ListTaskResponse> toListTaskResponse(List<ListTask> tasks);

}
