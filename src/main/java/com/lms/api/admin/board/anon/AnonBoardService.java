package com.lms.api.admin.board.anon;


import com.lms.api.admin.File.S3FileStorageService;
import com.lms.api.admin.File.dto.FileMeta;
import com.lms.api.admin.board.anon.dto.*;
import com.lms.api.common.entity.board.AnonBoardEntity;
import com.lms.api.common.entity.board.AnonBoardFileEntity;
import com.lms.api.common.entity.board.QAnonBoardEntity;
import com.lms.api.common.exception.ApiErrorCode;
import com.lms.api.common.exception.ApiException;
import com.lms.api.common.repository.board.AnonBoardFileRepository;
import com.lms.api.common.repository.board.AnonBoardRepository;
import com.lms.api.common.util.ObjectUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnonBoardService {
    private final S3FileStorageService s3FileStorageService;
    private final AnonBoardRepository anonBoardRepository;
    private final AnonBoardFileRepository anonBoardFileRepository;
    private final AnonBoardServiceMapper anonBoardServiceMapper;

    @Transactional
    public String createAnonBoard(String loginId, CreateAnonBoard createAnonBoard) {
        String boardId = "BAN" + System.nanoTime();

        AnonBoardEntity anonBoardEntity = AnonBoardEntity.builder()
                .id(boardId)
                .title(createAnonBoard.getTitle())
                .content(createAnonBoard.getContent())
                .createdBy(createAnonBoard.getCreatedBy())
                .modifiedBy(createAnonBoard.getCreatedBy())
                .view(0)
                .build();

        List<FileMeta> uploadFiles = s3FileStorageService.upload(createAnonBoard.getMultipartFiles(), "board/anon");

        for (FileMeta fileMeta : uploadFiles) {
            AnonBoardFileEntity fileEntity = AnonBoardFileEntity.builder()
                    .fileName(fileMeta.getS3Key())
                    .originalFileName(fileMeta.getOriginalFileName())
                    .createdBy(anonBoardEntity.getCreatedBy())
                    .modifiedBy(anonBoardEntity.getCreatedBy())
                    .build();

            anonBoardEntity.addFile(fileEntity); // ✅ 연관관계 설정, file.setAnonBoardEntity(this) 포함됨
        }

        anonBoardRepository.save(anonBoardEntity); // ✅ CascadeType.ALL에 의해 자식도 자동 저장
        return boardId;
    }

    @Transactional
    public Page<ListAnonBoard> listAnonBoard(SearchAnonBoard searchAnonBoard) {
        QAnonBoardEntity qAnonBoardEntity = QAnonBoardEntity.anonBoardEntity;
        BooleanExpression where = Expressions.TRUE;

        if(searchAnonBoard.hasSearch()) {
            switch (searchAnonBoard.getSearch()){
                case "all" :
                    where = where.and(
                            qAnonBoardEntity.title.contains(searchAnonBoard.getKeyword())
                                    .or(qAnonBoardEntity.content.contains(searchAnonBoard.getKeyword()))
                    );
                    break;
                case "title":
                    where = where.and(
                            qAnonBoardEntity.title.contains(searchAnonBoard.getKeyword())
                    );
                    break;
                case "content":
                    where = where.and(
                            qAnonBoardEntity.content.contains(searchAnonBoard.getKeyword())
                    );
                    break;
                default:
                    break;

            }
        }

        Page<AnonBoardEntity> boardPage  = anonBoardRepository.findAll(where, searchAnonBoard.toPageRequest());

        List<ListAnonBoard> list = boardPage.getContent().stream()
                .map(anonBoardEntity -> ListAnonBoard.builder()
                        .id(anonBoardEntity.getId())
                        .title(anonBoardEntity.getTitle())
                        .createDate(anonBoardEntity.getCreatedOn().toLocalDate())
                        .view(anonBoardEntity.getView())
                        .fileCount(anonBoardEntity.getAnonBoardFileEntities().size())
                        .build()
                ).toList();

        return new PageImpl<>(list, boardPage.getPageable(), boardPage.getTotalElements());

    }

    @Transactional
    public GetAnonBoard getAnonBoard(String loginId, String anonBoardId) {
        AnonBoardEntity anonBoardEntity = anonBoardRepository.findById(anonBoardId)
                .orElseThrow(()-> new ApiException(ApiErrorCode.ANONBOARD_NOT_FOUND));

        boolean isMine = anonBoardEntity.getCreatedBy().equals(loginId);

        int view = anonBoardEntity.getView() +1 ;
        anonBoardEntity.setView(view);

        List<GetAnonBoard.AnonBoardFile> anonBoardFiles = anonBoardEntity.getAnonBoardFileEntities()
                .stream()
                .map(file -> GetAnonBoard.AnonBoardFile.builder()
                        .anonBoardFileId(file.getId())
                        .originalFileName(file.getOriginalFileName())
                        .url(s3FileStorageService.getUrl(file.getFileName()))
                        .build()
                )
                .toList();
        return GetAnonBoard.builder()
                .id(anonBoardEntity.getId())
                .title(anonBoardEntity.getTitle())
                .content(anonBoardEntity.getContent())
                .view(view)
                .mine(isMine)
                .files(anonBoardFiles)
                .build();
    }

    @Transactional
    public void updateAnonBoardRequest(String anonBoardId, String loginId, UpdateAnonBoardRequest updateAnonBoardRequest) {
        AnonBoardEntity anonBoardEntity = anonBoardRepository.findById(anonBoardId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.ANONBOARD_NOT_FOUND));

        if( !anonBoardEntity.getCreatedBy().equals(loginId)){
            throw new ApiException(ApiErrorCode.ACCESS_DENIED);
        }

        // s3에서 삭제
        if(ObjectUtils.isNotEmpty(updateAnonBoardRequest.getDeleteFileIds())){
            updateAnonBoardRequest.getDeleteFileIds()
                    .forEach( fileId -> {
                        anonBoardEntity.getAnonBoardFileEntities().stream()
                                .filter( anonBoardFileEntity -> anonBoardFileEntity.getId().equals(fileId))
                                .findFirst()
                                .ifPresent(anonBoardFileEntity -> {
                                    String s3Key = anonBoardFileEntity.getFileName();
                                    if(s3Key != null && !s3Key.isBlank()){
                                        s3FileStorageService.delete(s3Key);
                                    }
                                });
                    });
        }
        // 추가된 파일 업로드
        List<FileMeta> uploadFiles = s3FileStorageService.upload(updateAnonBoardRequest.getFiles(),"/board/anon");

        // 기존 파일들
        List<AnonBoardFileEntity> anonBoardFileEntities = anonBoardEntity.getAnonBoardFileEntities()
                .stream()
                .filter(file -> updateAnonBoardRequest.getDeleteFileIds() == null || // 삭제할 파일 리스트에 없음
                        !updateAnonBoardRequest.getDeleteFileIds().contains(file.getId())) // 삭제할 파일 리스트에 현재 파일 식별키 없음
                .toList();

        // 추가된 파일
        List<AnonBoardFileEntity> newFiles = uploadFiles.stream()
                .map(file -> AnonBoardFileEntity.builder()
                        .fileName(file.getS3Key())
                        .originalFileName(file.getOriginalFileName())
                        .modifiedBy(loginId)
                        .createdBy(loginId)
                        .anonBoardEntity(anonBoardEntity)
                        .build()
                ).collect(Collectors.toList());

        // 컬렉션 재구성
        anonBoardEntity.getAnonBoardFileEntities().clear(); // 연결 끊기
        anonBoardEntity.getAnonBoardFileEntities().addAll(anonBoardFileEntities);
        anonBoardEntity.getAnonBoardFileEntities().addAll(newFiles);

        updateAnonBoardRequest.setModifiedBy(loginId);
        AnonBoardEntity anonBoard = anonBoardServiceMapper.toAnonBoardEntity(updateAnonBoardRequest, anonBoardEntity);

        anonBoardRepository.save(anonBoard);
    }
}



