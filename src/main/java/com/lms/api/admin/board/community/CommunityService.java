package com.lms.api.admin.board.community;


import com.lms.api.admin.File.S3FileStorageService;
import com.lms.api.admin.File.dto.FileMeta;
import com.lms.api.admin.board.community.dto.*;
import com.lms.api.common.entity.community.*;
import com.lms.api.common.exception.ApiErrorCode;
import com.lms.api.common.exception.ApiException;
import com.lms.api.common.repository.community.*;
import com.lms.api.common.util.AuthUtils;
import com.lms.api.common.util.DateTimeUtil;
import com.lms.api.common.util.FileUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommunityService {
    private final S3FileStorageService s3FileStorageService;
    private final AuthUtils authUtils;
    private final CommunityServiceMapper communityServiceMapper;
    private final CommunityRepository communityRepository;
    private final CommunityBoardRepository communityBoardRepository;
    private final CommunityBoardFileRepository communityBoardFileRepository;
    private final CommunityBoardCommentRepository communityBoardCommentRepository;
    private final CommunityBoardReplyRepository communityBoardReplyRepository;
    @Transactional
    public String createCommunity(String loginId, CreateCommunityRequest createCommunityRequest) {

        FileMeta fileMeta = null;

        if (createCommunityRequest.getFile() != null ) {
            String ext = FileUtil.getFileExtension(createCommunityRequest.getFile().getOriginalFilename());
            if (!FileUtil.isAllowedImageExtension(ext)) {
                throw new ApiException(ApiErrorCode.UNSUPPORTED_FORMAT_ERROR);
            }

            fileMeta = s3FileStorageService.upload(createCommunityRequest.getFile(), "board/community");
        }

        String id = "C" +System.nanoTime();

        CommunityEntity communityEntity = CommunityEntity.builder()
                .id(id)
                .title(createCommunityRequest.getTitle())
                .description(createCommunityRequest.getDescription())
                .hasProject(false)
                .createdBy(loginId)
                .modifiedBy(loginId)
                .originalFileName(fileMeta != null ? fileMeta.getOriginalFileName() : null)
                .fileName(fileMeta != null ? fileMeta.getS3Key() : null)
                .build();

        communityRepository.save(communityEntity);
        return id;
    }

    public Page<ListCommunity> listCommunity(SearchCommunity searchCommunity) {
        QCommunityEntity qCommunityEntity = QCommunityEntity.communityEntity;

        BooleanExpression where = Expressions.TRUE;

        if(searchCommunity.hasSearch()) {
            switch (searchCommunity.getSearch()){
                case "all" :
                    where = where.and(
                            qCommunityEntity.title.contains(searchCommunity.getKeyword())
                                    .or(qCommunityEntity.description.contains(searchCommunity.getKeyword()))
                    );
                    break;
                case "title" :
                    where = where.and(
                            qCommunityEntity.title.contains(searchCommunity.getKeyword())
                    );
                    break;
                case "description" :
                    where = where.and(
                            qCommunityEntity.description.contains(searchCommunity.getKeyword())
                    );
                    break;
                default:
                    break;
            }
        }
        Page<CommunityEntity> communityPage = communityRepository.findAll(where, searchCommunity.toPageRequest());

        List<ListCommunity> list = communityPage.getContent().stream()
                .map(community -> ListCommunity.builder()
                        .id(community.getId())
                        .url(s3FileStorageService.getUrl(community.getFileName()))
                        .title(community.getTitle())
                        .description(community.getDescription())
                        .createdBy(community.getCreatedBy())
                        .isScrapped(true) //TODO 추후 수정 필요
                        .boardId(null) // TODO 추후 수정 필요
                        .build()
                ).toList();

        return new PageImpl<>(list, communityPage.getPageable(), communityPage.getTotalElements());
    }

    @Transactional
    public String createBoard(String loginId, CreateCommunityBoardRequest createCommunityBoardRequest, String communityId) {
        CommunityEntity communityEntity = communityRepository.findById(communityId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.COMMUNITY_NOT_FOUND));

        boolean pinned = communityEntity.getCreatedBy().equals(loginId) // 커뮤니티 작성자와 지금 로그인한 사용자가 같은가?
                && createCommunityBoardRequest.isPinned(); // 사용자가 게시글을 조정으로 하고 싶다고 설정했는가?

        // 작성자가 false로 유지 되는 이유 >> 위에는 && 연산자로 둘 중 하나라도 false면 false임.

        String id = "CB" + System.nanoTime();
        CommunityBoardEntity communityBoardEntity = communityServiceMapper.toCommunityBoardEntity(
                loginId, createCommunityBoardRequest, id, pinned, communityEntity
        );

        // 업로드 파일 추적용 리스트
        List<FileMeta> uploadedFiles = new ArrayList<>();

        try {
            // S3 업로드
            uploadedFiles = s3FileStorageService.upload(createCommunityBoardRequest.getFiles(), "community/board");

            // DB 저장
            communityBoardRepository.save(communityBoardEntity);

            for (FileMeta file : uploadedFiles) {
                CommunityBoardFileEntity communityBoardFileEntity =
                        communityServiceMapper.toCommunityBoardFileEntity(
                                file.getS3Key(),
                                file.getOriginalFileName(),
                                loginId,
                                communityBoardEntity
                        );
                communityBoardFileRepository.save(communityBoardFileEntity);
            }
            return id;
        } catch (Exception e) {
            // 예외 발생 시 업로드한 파일 삭제 (보상 로직)
            for (FileMeta file : uploadedFiles) {
                try {
                    s3FileStorageService.delete(file.getS3Key());
                } catch (Exception ex) {
                    // 삭제 실패는 로그만 남기고 무시
                    log.warn("S3 삭제 실패: {}", file.getS3Key(), ex);
                }
            }
            throw e; // 예외 다시 던짐 → 트랜잭션 롤백됨
        }
    }

    @Transactional
    public Page<ListCommunityBoard> listBoard(String communityId, SearchCommunityBoard searchCommunityBoard) {
        CommunityEntity communityEntity = communityRepository.findById(communityId)
                .orElseThrow(()-> new ApiException(ApiErrorCode.COMMUNITY_NOT_FOUND));

        QCommunityBoardEntity qCommunityBoardEntity = QCommunityBoardEntity.communityBoardEntity;
        BooleanExpression where = Expressions.TRUE;

        if(searchCommunityBoard.hasSearch()){
            switch (searchCommunityBoard.getSearch()){
                case "all" :
                    where = where.and(
                            qCommunityBoardEntity.title.contains(searchCommunityBoard.getKeyword())
                                    .or(qCommunityBoardEntity.content.contains(searchCommunityBoard.getKeyword()))
                    );
                    break;
                case "title" :
                    where = where.and(
                            qCommunityBoardEntity.title.contains(searchCommunityBoard.getKeyword())
                    );
                    break;
                case "content" :
                    where = where.and(
                            qCommunityBoardEntity.content.contains(searchCommunityBoard.getKeyword())
                    );
                    break;
                default:
                    break;
            }
        }
        Page<CommunityBoardEntity> boardPage = communityBoardRepository.findAll(where, searchCommunityBoard.toPageRequest());

        List<ListCommunityBoard> list = boardPage.getContent().stream()
                .map(board -> ListCommunityBoard.builder()
                        .id(board.getId())
                        .title(board.getTitle())
                        .view(board.getView())
                        .createdOn(DateTimeUtil.formatConditionalDateTime(board.getCreatedOn()))
                        .createdBy(board.getCreatedBy())
                        .commentCount(board.getCommunityBoardFileEntities().size())
                        .commentCount(board.getCommunityBoardCommentEntities().size())
                        .build()
                ).toList();

        return new PageImpl<>(list, boardPage.getPageable(), boardPage.getTotalElements());

    }

    @Transactional
    public Long createComment(String loginId, String boardId, CreateCommunityCommentRequest createCommunityCommentRequest) {
        CommunityBoardEntity communityBoardEntity = communityBoardRepository.findById(boardId)
                .orElseThrow(()-> new ApiException(ApiErrorCode.COMMUNITY_COMMENT_NOT_FOUND));
        CommunityBoardCommentEntity communityBoardCommentEntity = CommunityBoardCommentEntity.builder()
                .comment(createCommunityCommentRequest.getComment())
                .isSecret(createCommunityCommentRequest.getIsSecret())
                .createdBy(loginId)
                .modifiedBy(loginId)
                .communityBoardEntity(communityBoardEntity)
                .build();

        communityBoardCommentRepository.save(communityBoardCommentEntity);
        return communityBoardCommentEntity.getId();
    }
    @Transactional
    public Long createReply(String loginId, Long commentId, CreateCommunityReply createCommunityReply) {
        CommunityBoardCommentEntity communityBoardCommentEntity = communityBoardCommentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.COMMUNITY_COMMENT_NOT_FOUND));
        CommunityBoardReplyEntity communityBoardReplyEntity = CommunityBoardReplyEntity.builder()
                .reply(createCommunityReply.getReply())
                .isSecret(createCommunityReply.getIsSecret())
                .modifiedBy(loginId)
                .createdBy(loginId)
                .communityBoardCommentEntity(communityBoardCommentEntity)
                .build();

        communityBoardReplyRepository.save(communityBoardReplyEntity);
        return communityBoardReplyEntity.getId();
    }

    public GetBoard getBoard(String loginId, String boardId) {
        CommunityBoardEntity communityBoardEntity = communityBoardRepository.findById(boardId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.COMMUNITY_BOARD_NOT_FOUND));

        List<CommunityBoardFileEntity> fileEntities = communityBoardFileRepository.findByCommunityBoardEntity(communityBoardEntity);
        List<GetBoard.FileMeta> files =  fileEntities.stream()
                .map(file -> GetBoard.FileMeta.builder()
                        .boardFileId(file.getId())
                        .originalFileName(file.getOriginalFileName())
                        .url(s3FileStorageService.getUrl(file.getFileName()))
                        .build())
                .toList();
        return GetBoard.builder()
                .id(communityBoardEntity.getId())
                .title(communityBoardEntity.getTitle())
                .content(communityBoardEntity.getContent())
                .createdOn(communityBoardEntity.getCreatedOn().toLocalDate())
                .createdBy(communityBoardEntity.getCreatedBy())
                .files(files)
                .view(communityBoardEntity.getView())
                .isMine(communityBoardEntity.getCreatedBy().equals(loginId))
                .build();
    }

    @Transactional
    public List<ListCommunityBoardComment> listComment(String loginId, String boardId) {
        CommunityBoardEntity communityBoardEntity = communityBoardRepository.findById(boardId)
                .orElseThrow(()-> new ApiException(ApiErrorCode.COMMUNITY_BOARD_NOT_FOUND));

        List<CommunityBoardCommentEntity> communityBoardCommentEntities = communityBoardCommentRepository.findByCommunityBoardEntity(communityBoardEntity);
        return communityBoardCommentEntities.stream()
                .map( commentEntity -> {
                    boolean commentMine = commentEntity.getCreatedBy().equals(loginId);
                    String modifiedOn = DateTimeUtil.formatConditionalDateTime(commentEntity.getModifiedOn());
                    ListCommunityBoardComment comment = communityServiceMapper.toListCommunityBoardComment(commentEntity, commentMine, modifiedOn, boardId);

                    List<CommunityBoardReplyEntity> replyEntities = communityBoardReplyRepository.findByCommunityBoardCommentEntity(commentEntity);
                    List<ListCommunityBoardComment.Reply> replies = replyEntities.stream()
                            .map( entity -> {
                                boolean replyMine = entity.getCreatedBy().equals(loginId);
                                String replyDate = DateTimeUtil.formatConditionalDateTime(entity.getModifiedOn());
                                return communityServiceMapper.toReply(entity,replyMine, replyDate);
                            }).toList();
                    comment.setReplies(replies);
                    return comment;
                })
                .collect(Collectors.toList());

    }

    @Transactional
    public void updateReply(String loginId, Long commentId, Long replyId, UpdateCommunityReply updateCommunityReply) {
        CommunityBoardCommentEntity commentEntity = communityBoardCommentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.COMMUNITY_COMMENT_NOT_FOUND));
        CommunityBoardReplyEntity replyEntity = communityBoardReplyRepository.findById(replyId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.COMMUNITY_REPLY_NOT_FOUND));

        if(!replyEntity.getCreatedBy().equals(loginId)){
            throw new ApiException(ApiErrorCode.ACCESS_DENIED);
        }
        replyEntity.setReply(updateCommunityReply.getReply());
        replyEntity.setSecret(updateCommunityReply.getIsSecret());
        replyEntity.setModifiedBy(loginId);

        communityBoardReplyRepository.save(replyEntity);
    }

    @Transactional
    public void deleteReply(String loginId, Long commentId, Long replyId) {
        CommunityBoardCommentEntity commentEntity = communityBoardCommentRepository.findById(commentId)
                        .orElseThrow(()-> new ApiException(ApiErrorCode.COMMUNITY_COMMENT_NOT_FOUND));
        CommunityBoardReplyEntity replyEntity = communityBoardReplyRepository.findById(replyId)
                        .orElseThrow(() -> new ApiException(ApiErrorCode.COMMUNITY_REPLY_NOT_FOUND));
        authUtils.validateOwnerOrAdmin(loginId, replyEntity);
        communityBoardReplyRepository.deleteById(replyId);
    }

    @Transactional
    public void deleteComment(String loginId, Long commentId) {
        CommunityBoardCommentEntity commentEntity = communityBoardCommentRepository.findById(commentId)
                .orElseThrow(()-> new ApiException(ApiErrorCode.COMMUNITY_COMMENT_NOT_FOUND));

        authUtils.validateOwnerOrAdmin(loginId,commentEntity);

        // 댓글 실제 삭제는 아님.
        commentEntity.softDelete(loginId);
    }
}


/**
 * -->>> 권장 방식
 * @Transactional
 * public String createBoard(String loginId, CreateCommunityBoardRequest createCommunityBoardRequest, String communityId) {
 *     CommunityEntity communityEntity = communityRepository.findById(communityId)
 *             .orElseThrow(() -> new ApiException(ApiErrorCode.COMMUNITY_NOT_FOUND));
 *
 *     boolean pinned = communityEntity.getCreatedBy().equals(loginId) && createCommunityBoardRequest.isPinned();
 *
 *     String id = "CB" + System.nanoTime();
 *
 *     CommunityBoardEntity communityBoardEntity = communityServiceMapper.toCommunityBoardEntity(
 *             loginId, createCommunityBoardRequest, id, pinned, communityEntity
 *     );
 *
 *     List<FileMeta> uploadedFiles = new ArrayList<>();
 *
 *     try {
 *         uploadedFiles = s3FileStorageService.upload(createCommunityBoardRequest.getFiles(), "community/board");
 *
 *         for (FileMeta file : uploadedFiles) {
 *             CommunityBoardFileEntity fileEntity = communityServiceMapper.toCommunityBoardFileEntity(
 *                     file.getS3Key(),
 *                     file.getOriginalFileName(),
 *                     loginId
 *             );
 *             communityBoardEntity.addFile(fileEntity); // ✅ 연관관계 설정
 *         }
 *
 *         communityBoardRepository.save(communityBoardEntity);
 *
 *         return id;
 *     } catch (Exception e) {
 *         for (FileMeta file : uploadedFiles) {
 *             try {
 *                 s3FileStorageService.delete(file.getS3Key());
 *             } catch (Exception ex) {
 *                 log.warn("S3 삭제 실패: {}", file.getS3Key(), ex);
 *             }
 *         }
 *         throw e;
 *     }
 * }
 */
