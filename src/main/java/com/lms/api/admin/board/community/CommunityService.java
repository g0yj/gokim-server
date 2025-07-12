package com.lms.api.admin.board.community;


import com.lms.api.admin.File.S3FileStorageService;
import com.lms.api.admin.File.dto.FileMeta;
import com.lms.api.admin.board.community.dto.*;
import com.lms.api.common.config.JpaConfig;
import com.lms.api.common.dto.ScrapTableType;
import com.lms.api.common.entity.QScrapEntity;
import com.lms.api.common.entity.ScrapEntity;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.entity.community.*;
import com.lms.api.common.exception.ApiErrorCode;
import com.lms.api.common.exception.ApiException;
import com.lms.api.common.repository.ScrapRepository;
import com.lms.api.common.repository.UserRepository;
import com.lms.api.common.repository.community.*;
import com.lms.api.common.util.AuthUtils;
import com.lms.api.common.util.DateTimeUtils;
import com.lms.api.common.util.FileUploadUtils;
import com.lms.api.common.util.FileUtils;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommunityService {
    private final JpaConfig jpaConfig;
    private final S3FileStorageService s3FileStorageService;
    private final AuthUtils authUtils;
    private final CommunityServiceMapper communityServiceMapper;
    private final CommunityRepository communityRepository;
    private final CommunityBoardRepository communityBoardRepository;
    private final CommunityBoardFileRepository communityBoardFileRepository;
    private final CommunityBoardCommentRepository communityBoardCommentRepository;
    private final CommunityBoardReplyRepository communityBoardReplyRepository;
    private final UserRepository userRepository;
    private final ScrapRepository scrapRepository;
    @Transactional
    public String createCommunity(String loginId, CreateCommunityRequest request) {
        MultipartFile file = request.getFile();

        //  파일 개수 제한 : 이미 MultipartFile로 넘어와서 필요 없긴한데 ..
        FileUtils.validateSingleFileField(
                FileUtils.wrapToListIfNotNull(file),
                1
        );

        //  확장자 검증
        if (file != null) {
            String ext = FileUtils.getFileExtension(file.getOriginalFilename());
            if (!FileUtils.isAllowedImageExtension(ext)) {
                throw new ApiException(ApiErrorCode.UNSUPPORTED_FORMAT_ERROR);
            }
        }

        //  파일 업로드 + 보상 로직
        FileMeta fileMeta = FileUploadUtils.uploadOneWithRollback(
                file,
                "community",
                s3FileStorageService
        );

        //  커뮤니티 엔티티 생성 및 저장
        String id = "C" + System.nanoTime();

        CommunityEntity community = CommunityEntity.builder()
                .id(id)
                .title(request.getTitle())
                .description(request.getDescription())
                .createdBy(loginId)
                .modifiedBy(loginId)
                .originalFileName(fileMeta != null ? fileMeta.getOriginalFileName() : null)
                .fileName(fileMeta != null ? fileMeta.getS3Key() : null)
                .build();

        communityRepository.save(community);

        // boardId
        CreateCommunityBoardRequest board = new CreateCommunityBoardRequest();
        board.setTitle("임시 게시글");
        String boardId = createBoard(loginId, board, id);
        List<CommunityBoardEntity> boardEntities = communityBoardRepository.findAllById(Collections.singleton(boardId));

        community.setCommunityBoardEntities(null); // ← 연관관계 끊기
        communityRepository.save(community);       // ← 끊긴 상태로 flush
        deleteBoard(loginId, boardId);             // ← 이제 안전하게 삭제

        return id;
    }

    @Transactional
    public Page<ListCommunity> listCommunity(String loginId, SearchCommunity searchCommunity) {
        QCommunityEntity qCommunityEntity = QCommunityEntity.communityEntity;
        QScrapEntity qScrapEntity = QScrapEntity.scrapEntity;
        QCommunityBoardEntity qCommunityBoardEntity = QCommunityBoardEntity.communityBoardEntity;

        UserEntity userEntity = userRepository.findById(loginId)
                .orElseThrow(()-> new ApiException(ApiErrorCode.USER_NOT_FOUND));


        // 1. 검색 조건 동적 구성
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

        //2. QueryDSL 쿼리 실행
        List<Tuple> result = jpaConfig.queryFactory()
                // 어떤 컬럼을 조회할지 지정. 여러 필드를 지정하게 되면 Tuple 반환임
                .select(
                        qCommunityEntity.id,
                        qCommunityEntity.title,
                        qCommunityEntity.description,
                        qCommunityEntity.createdBy,
                        qCommunityEntity.fileName,
                        // 로그인 유저의 스크랩 여부를 EXISTS 서브쿼리로 구성
                        JPAExpressions.selectOne()
                                .from(qScrapEntity)
                                .where(
                                        qScrapEntity.userEntity.eq(userEntity)
                                                .and(qScrapEntity.targetId.eq(qCommunityEntity.id))
                                                .and(qScrapEntity.tableType.eq(ScrapTableType.COMMUNITY))
                                )
                                .exists(),
                        JPAExpressions.select(qCommunityBoardEntity.id.min())
                                        .from(qCommunityBoardEntity)
                                        .where(qCommunityBoardEntity.communityEntity.eq(qCommunityEntity))
                )
                .from(qCommunityEntity)
                .where(where)
                .offset(searchCommunity.getOffset()) // 몇 번째 row 부터 시작할지(페이지네이션 시작치)
                .limit(searchCommunity.getPageSize())
                .fetch(); // 쿼리 실행하여 List 반환

        // 3. 전체 갯수 (페이지네이션에 필요)
        Long totalCount = jpaConfig.queryFactory()
                .select(qCommunityEntity.count())
                .from(qCommunityEntity)
                .where(where)
                .fetchOne();

        //4. Tuple 결과를 DTO 매핑
        List<ListCommunity> list = result.stream()
                .map(tuple -> ListCommunity.builder()
                        .id(tuple.get(qCommunityEntity.id))
                        .title(tuple.get(qCommunityEntity.title))
                        .description(tuple.get(qCommunityEntity.description))
                        .createdBy(tuple.get(qCommunityEntity.createdBy))
                        .url(s3FileStorageService.getUrl(tuple.get(qCommunityEntity.fileName)))
                        .isScrapped(Boolean.TRUE.equals(tuple.get(5, Boolean.class))) // null 방지
                        .boardId(tuple.get(6, String.class)) // boardId는 Long으로 추정
                        .build())
                .toList();

        // 5. PageImpl로 리턴
        return new PageImpl<>(list, searchCommunity.toPageRequest(), totalCount);
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

        // 업로드
        List<FileMeta> uploaded = FileUploadUtils.uploadWithRollback(
                createCommunityBoardRequest.getFiles(),
                "community/board",
                s3FileStorageService
        );

        // 파일 엔티티 생성 및 연관관계 설정
        for (FileMeta meta : uploaded) {
            CommunityBoardFileEntity fileEntity = communityServiceMapper.toCommunityBoardFileEntity(
                    meta.getS3Key(),
                    meta.getOriginalFileName(),
                    loginId
            );
            communityBoardEntity.addFile(fileEntity); // 연관관계 설정
        }

        communityBoardRepository.save(communityBoardEntity);
        return id;
    }

    @Transactional
    public Page<ListCommunityBoard> listBoard(String communityId, SearchCommunityBoard searchCommunityBoard) {
        CommunityEntity communityEntity = communityRepository.findById(communityId)
                .orElseThrow(()-> new ApiException(ApiErrorCode.COMMUNITY_NOT_FOUND));

        QCommunityBoardEntity qCommunityBoardEntity = QCommunityBoardEntity.communityBoardEntity;
        BooleanExpression where = Expressions.TRUE;

        where = where.and(qCommunityBoardEntity.communityEntity.eq(communityEntity));

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
                        .createdOn(DateTimeUtils.formatConditionalDateTime(board.getCreatedOn()))
                        .createdBy(board.getCreatedBy())
                        .commentCount(board.getCommunityBoardFileEntities().size())
                        .commentCount(board.getCommunityBoardCommentEntities().size())
                        .communityId(communityEntity.getId())
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

    @Transactional
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
                .communityId(communityBoardEntity.getCommunityEntity().getId())
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
                    String modifiedOn = DateTimeUtils.formatConditionalDateTime(commentEntity.getModifiedOn());
                    ListCommunityBoardComment comment = communityServiceMapper.toListCommunityBoardComment(commentEntity, commentMine, modifiedOn, boardId);

                    List<CommunityBoardReplyEntity> replyEntities = communityBoardReplyRepository.findByCommunityBoardCommentEntity(commentEntity);
                    List<ListCommunityBoardComment.Reply> replies = replyEntities.stream()
                            .map( entity -> {
                                boolean replyMine = entity.getCreatedBy().equals(loginId);
                                String replyDate = DateTimeUtils.formatConditionalDateTime(entity.getModifiedOn());
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

    @Transactional
    public void updateComment(String loginId, Long commentId, UpdateCommunityComment updateCommunityComment) {
        CommunityBoardCommentEntity commentEntity = communityBoardCommentRepository.findById(commentId)
                .orElseThrow(()-> new ApiException(ApiErrorCode.COMMUNITY_COMMENT_NOT_FOUND));

        authUtils.validateOwner(loginId,commentEntity);

        commentEntity.setComment(updateCommunityComment.getComment());
        commentEntity.setSecret(updateCommunityComment.getIsSecret());
        commentEntity.setModifiedBy(loginId);

        communityBoardCommentRepository.save(commentEntity);

    }

    @Transactional
    public void updateBoard(String loginId, String boardId, UpdateCommunityBoard updateCommunityBoard) {
        CommunityBoardEntity boardEntity = communityBoardRepository.findById(boardId)
                .orElseThrow(()-> new ApiException(ApiErrorCode.COMMUNITY_BOARD_NOT_FOUND));
        authUtils.validateOwner(loginId, boardEntity);

        // 삭제 대상 ID
        List<Long> deleteFileIds = Optional.ofNullable(updateCommunityBoard.getDeleteFileIds()).orElse(List.of());

        // 삭제 대상 추출
        List<CommunityBoardFileEntity> deleteFilesEntity = boardEntity.getCommunityBoardFileEntities().stream()
                .filter(f -> deleteFileIds.contains(f.getId()))
                .toList();

        // s3 삭제 + 업로드
        List<FileMeta> uploadedFiles = FileUploadUtils.updateFilesWithRollback(
                updateCommunityBoard.getFiles(),
                deleteFilesEntity,
                CommunityBoardFileEntity::getFileName,
                files -> s3FileStorageService.upload(files, "community/board"),
                s3FileStorageService::delete
        );

        // 기존 파일 중 삭제 대상 제외한 것 유지
        List<CommunityBoardFileEntity> remainedFiles = boardEntity.getCommunityBoardFileEntities().stream()
                .filter(f -> !deleteFileIds.contains(f.getId()))
                .toList();

        // 컬렉션 재구성
        boardEntity.getCommunityBoardFileEntities().clear();
        boardEntity.getCommunityBoardFileEntities().addAll(remainedFiles);

        // 새로 업로드된 파일 DB 저장
        for (FileMeta meta : uploadedFiles) {
            CommunityBoardFileEntity fileEntity = CommunityBoardFileEntity.builder()
                    .fileName(meta.getS3Key())
                    .originalFileName(meta.getOriginalFileName())
                    .createdBy(loginId)
                    .modifiedBy(loginId)
                    .communityBoardEntity(boardEntity)
                    .build();
            boardEntity.addFile(fileEntity); // 연관관계 자동 설정
        }

        // 본문 수정
        boardEntity.setModifiedBy(loginId);
        boardEntity = communityServiceMapper.toCommunityBoardEntity(updateCommunityBoard,boardEntity );
        communityBoardRepository.save(boardEntity);

    }

    @Transactional
    public void deleteBoard(String loginId, String boardId) {
        CommunityBoardEntity boardEntity = communityBoardRepository.findById(boardId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.COMMUNITY_BOARD_NOT_FOUND));

        authUtils.validateOwnerOrAdmin(loginId, boardEntity);

        // 연관 파일 엔티티 복사 (영속성 컨텍스트 보호)
        List<CommunityBoardFileEntity> fileEntities = new ArrayList<>(boardEntity.getCommunityBoardFileEntities());

        // S3 파일 삭제
        FileUploadUtils.deleteS3Files(
                fileEntities,
                CommunityBoardFileEntity::getFileName,
                s3FileStorageService::delete
        );

        boardEntity.getCommunityBoardFileEntities().clear(); // 연관관계 끊기 + orphanRemoval 동작
        communityBoardRepository.delete(boardEntity);
    }

    @Transactional
    public GetCommunity getCommunity(String loginId, String id) {
        CommunityEntity communityEntity = communityRepository.findById(id)
                .orElseThrow(()-> new ApiException(ApiErrorCode.COMMUNITY_NOT_FOUND));

        boolean isMine = authUtils.isOwner(loginId,communityEntity);
        String url = s3FileStorageService.getUrl(communityEntity.getFileName());
        String modifiedOn = DateTimeUtils.formatConditionalDateTime(communityEntity.getModifiedOn());
        return communityServiceMapper.toGetCommunity(communityEntity,isMine,url, modifiedOn);
    }

    @Transactional
    public void updateCommunity(String loginId, String communityId, UpdateCommunity updateCommunity) {
        CommunityEntity communityEntity = communityRepository.findById(communityId)
                .orElseThrow(()-> new ApiException(ApiErrorCode.COMMUNITY_NOT_FOUND));
        authUtils.validateOwner(loginId,communityEntity);

        MultipartFile file = updateCommunity.getFile();
        FileUtils.validateImageFileExtension(file);

        if(file != null && !file.isEmpty()){
            String oldKey  = communityEntity.getFileName();
            if(oldKey  != null && !oldKey.isBlank()){
                s3FileStorageService.delete(oldKey );
            }

            FileMeta newFile =  s3FileStorageService.upload(file,"community");

            communityEntity.setFileName(newFile.getS3Key());
            communityEntity.setOriginalFileName(newFile.getOriginalFileName());
        }
        communityEntity.setModifiedBy(loginId);
        communityEntity = communityServiceMapper.toCommunityEntity(updateCommunity, communityEntity);

        communityRepository.save(communityEntity);

    }

    @Transactional
    public void deleteCommunity(String loginId, String communityId) {
        CommunityEntity communityEntity = communityRepository.findById(communityId)
                .orElseThrow(()-> new ApiException(ApiErrorCode.COMMUNITY_NOT_FOUND));

        authUtils.validateOwnerOrAdmin(loginId, communityEntity);

        s3FileStorageService.delete(communityEntity.getFileName());

        // 커뮤니티 내 게시글 전체 조회
        List<CommunityBoardEntity> boardEntities = communityEntity.getCommunityBoardEntities();
        for (CommunityBoardEntity board : boardEntities) {
            CommunityBoardEntity boardEntity = communityBoardRepository.findById(board.getId())
                    .orElseThrow(() -> new ApiException(ApiErrorCode.COMMUNITY_BOARD_NOT_FOUND));

            // 연관 파일 엔티티 복사 (영속성 컨텍스트 보호)
            List<CommunityBoardFileEntity> fileEntities = new ArrayList<>(boardEntity.getCommunityBoardFileEntities());

            // S3 파일 삭제
            FileUploadUtils.deleteS3Files(
                    fileEntities,
                    CommunityBoardFileEntity::getFileName,
                    s3FileStorageService::delete
            );

            boardEntity.getCommunityBoardFileEntities().clear(); // 연관관계 끊기 + orphanRemoval 동작
            communityBoardRepository.delete(boardEntity);
        }

        communityRepository.deleteById(communityId);

    }
    @Transactional(readOnly = false)
    public void addScrapped(String loginId, String communityId) {
        UserEntity userEntity = userRepository.findById(loginId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));

        ScrapEntity scrapEntity = ScrapEntity.builder()
                .tableType(ScrapTableType.COMMUNITY)
                .targetId(communityId)
                .userEntity(userEntity)
                .build();

        scrapRepository.save(scrapEntity);
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
