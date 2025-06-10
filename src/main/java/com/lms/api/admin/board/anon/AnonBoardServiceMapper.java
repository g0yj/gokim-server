package com.lms.api.admin.board.anon;


import com.lms.api.admin.board.anon.dto.ListAnonBoard;
import com.lms.api.admin.board.anon.dto.UpdateAnonBoardRequest;
import com.lms.api.common.entity.board.AnonBoardEntity;
import com.lms.api.common.mapper.ServiceMapper;
import com.lms.api.common.mapper.ServiceMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring", config = ServiceMapperConfig.class, uses = {ServiceMapper.class})
public interface AnonBoardServiceMapper {

    @Mapping(target = "createDate", source = "createdOn")
    ListAnonBoard toListAnonBoard(AnonBoardEntity anonBoardEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "anonBoardFileEntities", ignore = true)
    @Mapping(target = "createdBy", source ="updateAnonBoardRequest.modifiedBy" )
    AnonBoardEntity toAnonBoardEntity(UpdateAnonBoardRequest updateAnonBoardRequest, @MappingTarget AnonBoardEntity anonBoardEntity);
}
