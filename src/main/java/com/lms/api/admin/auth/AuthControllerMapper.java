package com.lms.api.admin.auth;

import com.lms.api.common.mapper.ControllerMapper;
import com.lms.api.common.mapper.ControllerMapperConfig;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = ControllerMapperConfig.class, uses = {
    ControllerMapper.class})
public interface AuthControllerMapper {


}
