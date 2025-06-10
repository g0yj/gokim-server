package com.lms.api.admin.board.anon;


import com.lms.api.admin.board.anon.dto.ListAnonBoard;
import com.lms.api.common.entity.board.AnonBoardEntity;
import com.lms.api.common.mapper.ServiceMapper;
import com.lms.api.common.mapper.ServiceMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", config = ServiceMapperConfig.class, uses = {ServiceMapper.class})
public interface AnonBoardServiceMapper {

    @Mapping(target = "createDate", source = "createdOn")
    ListAnonBoard toListAnonBoard(AnonBoardEntity anonBoardEntity);
}
