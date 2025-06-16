package com.lms.api.admin.board.community;


import com.lms.api.admin.File.dto.FileMeta;
import com.lms.api.admin.board.community.dto.*;
import com.lms.api.common.entity.community.*;
import com.lms.api.common.mapper.ServiceMapper;
import com.lms.api.common.mapper.ServiceMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;


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

    @Mapping(target = "commentMine", source = "commentMine" )
    @Mapping(target = "modifiedOn", source = "modifiedOn" )
    @Mapping(target = "boardId", source = "boardId" )
    @Mapping(target = "isSecret", source = "communityBoardCommentEntity.isSecret" )
    @Mapping(target = "deleted", source = "communityBoardCommentEntity.deleted" )
    ListCommunityBoardComment toListCommunityBoardComment(CommunityBoardCommentEntity communityBoardCommentEntity, boolean commentMine, String modifiedOn, String boardId);
    @Mapping(target = "replyId", source = "reply.id")
    @Mapping(target = "replyMine", source = "replyMine")
    @Mapping(target = "modifiedOn", source = "replyDate")
    @Mapping(target = "isSecret", source = "reply.isSecret")
    ListCommunityBoardComment.Reply toReply(CommunityBoardReplyEntity reply, boolean replyMine, String replyDate);

    @Mapping(target = "id", ignore = true)
    CommunityBoardEntity toCommunityBoardEntity (UpdateCommunityBoard updateCommunityBoard, @MappingTarget CommunityBoardEntity communityBoardEntity);

    @Mapping(target = "createdBy", source = "loginId")
    @Mapping(target = "modifiedBy", source = "loginId")
    CommunityBoardFileEntity toCommunityBoardFileEntity(FileMeta file, String loginId, CommunityBoardEntity boardEntity);

    @Mapping(target = "url", source = "url")
    @Mapping(target = "isMine", source = "isMine")
    @Mapping(target = "modifiedOn", source = "modifiedOn")
    GetCommunity toGetCommunity(CommunityEntity communityEntity, boolean isMine, String url, String modifiedOn);

    CommunityEntity toCommunityEntity(UpdateCommunity updateCommunity, @MappingTarget CommunityEntity communityEntity);
}
