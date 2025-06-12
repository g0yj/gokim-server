package com.lms.api.admin.board.community;


import com.lms.api.admin.board.community.dto.CreateCommunityBoardRequest;
import com.lms.api.common.entity.board.CommunityBoardEntity;
import com.lms.api.common.entity.board.CommunityBoardFileEntity;
import com.lms.api.common.entity.board.CommunityEntity;
import com.lms.api.common.mapper.ServiceMapper;
import com.lms.api.common.mapper.ServiceMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", config = ServiceMapperConfig.class, uses = {ServiceMapper.class})
public interface CommunityServiceMapper {

    @Mapping(target = "createdBy", source = "loginId")
    @Mapping(target = "id", source = "boardId")
    @Mapping(target = "pinned", source = "pinned")
    @Mapping(target = "communityEntity", source = "communityEntity")
    @Mapping(target = "title", source = "createCommunityBoardRequest.title")
    @Mapping(target = "content", source = "createCommunityBoardRequest.content")
    CommunityBoardEntity toCommunityBoardEntity(String loginId, CreateCommunityBoardRequest createCommunityBoardRequest, String boardId, boolean pinned, CommunityEntity communityEntity);

    @Mapping(target = "fileName", source = "fileName")
    @Mapping(target = "originalFileName", source ="originalFileName" )
    @Mapping(target = "createdBy", source = "loginId")
    @Mapping(target = "modifiedBy", source = "loginId")
    @Mapping(target = "communityBoardEntity", source = "communityBoardEntity")
    @Mapping(target = "id", ignore = true)
    CommunityBoardFileEntity toCommunityBoardFileEntity(String fileName, String originalFileName, String loginId, CommunityBoardEntity communityBoardEntity);

    /**
     * 권장 방식에서 사용 -> CommunityBoardEntity는 주입 안 함 – 연관관계는 addFile()에서 처리
     */
    @Mapping(target = "fileName", source = "fileName")
    @Mapping(target = "originalFileName", source = "originalFileName")
    @Mapping(target = "createdBy", source = "loginId")
    @Mapping(target = "modifiedBy", source = "loginId")
    CommunityBoardFileEntity toCommunityBoardFileEntity(String fileName, String originalFileName, String loginId);
}
