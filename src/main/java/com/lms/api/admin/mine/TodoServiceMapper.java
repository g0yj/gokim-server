package com.lms.api.admin.mine;


import com.lms.api.admin.mine.dto.ListTodoResponse;
import com.lms.api.common.entity.mine.TodoEntity;
import com.lms.api.common.mapper.ServiceMapper;
import com.lms.api.common.mapper.ServiceMapperConfig;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring", config = ServiceMapperConfig.class, uses = {ServiceMapper.class})
public interface TodoServiceMapper {

    ListTodoResponse.Todo toTodo(TodoEntity todoEntity);

    List<ListTodoResponse.Todo> toTodo(List<TodoEntity> entities);

}
