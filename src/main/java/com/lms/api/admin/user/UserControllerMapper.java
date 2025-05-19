package com.lms.api.admin.user;


import com.lms.api.admin.user.dto.CreateUser;
import com.lms.api.admin.user.dto.CreateUserRequest;
import com.lms.api.common.mapper.ControllerMapper;
import com.lms.api.common.mapper.ControllerMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", config = ControllerMapperConfig.class, uses = {
    ControllerMapper.class})
public interface UserControllerMapper {

    @Mapping(target = "multipartFile", source = "createUserRequest.file")
    @Mapping(target = "fileName", ignore = true)
    CreateUser toCreateUser(CreateUserRequest createUserRequest);

}
