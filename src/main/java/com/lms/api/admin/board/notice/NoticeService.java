package com.lms.api.admin.board.notice;


import com.lms.api.admin.File.S3FileStorageService;
import com.lms.api.admin.board.notice.dto.CreateNotice;
import com.lms.api.admin.board.notice.dto.*;
import com.lms.api.admin.File.dto.FileMeta;
import com.lms.api.admin.user.enums.UserRole;
import com.lms.api.common.config.JpaConfig;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.entity.board.NoticeEntity;
import com.lms.api.common.entity.board.NoticeFileEntity;
import com.lms.api.common.entity.board.QNoticeEntity;
import com.lms.api.common.exception.ApiErrorCode;
import com.lms.api.common.exception.ApiException;
import com.lms.api.common.repository.UserRepository;
import com.lms.api.common.repository.board.NoticeFileRepository;
import com.lms.api.common.repository.board.NoticeRepository;
import com.lms.api.common.util.DateTimeUtils;
import com.lms.api.common.util.ObjectUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {
    private final JpaConfig jpaConfig;
    private final S3FileStorageService s3FileStorageService;
    private final NoticeServiceMapper noticeServiceMapper;
    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;
    private final UserRepository userRepository;

    @Transactional
    public String createNotice(String loginId, CreateNotice createNotice) {

        UserEntity admin = userRepository.findByIdAndRole(createNotice.getCreatedBy(), UserRole.ADMIN)
                .orElseThrow(() -> new ApiException(ApiErrorCode.ACCESS_DENIED));

        String noticeId = "BN" + System.nanoTime();

        NoticeEntity noticeEntity = NoticeEntity.builder()
                .id(noticeId)
                .title(createNotice.getTitle())
                .content(createNotice.getContent())
                .pinned(createNotice.isPinned())
                .createdBy(loginId)
                .modifiedBy(loginId)
                .build();

        List<FileMeta> uploadedFiles = s3FileStorageService.upload(createNotice.getMultipartFiles(), "board/notice");

        List<NoticeFileEntity> fileEntities = uploadedFiles.stream()
                .map(fileMeta -> NoticeFileEntity.builder()
                            .fileName(fileMeta.getS3Key())
                            .originalFileName(fileMeta.getOriginalFileName())
                            .noticeEntity(noticeEntity)
                            .createdBy(createNotice.getCreatedBy())
                            .modifiedBy(createNotice.getCreatedBy())
                            .build())
                .collect(Collectors.toList());

        noticeEntity.setNoticeFileEntities(fileEntities);
        noticeRepository.save(noticeEntity);
        return noticeId;
    }

    @Transactional
    public Page<ListPageNotice> pageListNotice(String loginId, SearchNotice searchNotice) {
        QNoticeEntity qNoticeEntity = QNoticeEntity.noticeEntity;

        // 조건 만들기
        BooleanExpression where = Expressions.TRUE;

        // 조건: 작성자 , 제목
        if (searchNotice.hasSearch()) {
            switch (searchNotice.getSearch()) {
                case "all":
                    where = where.and(
                            qNoticeEntity.modifiedBy.contains(searchNotice.getKeyword())
                                    .or(qNoticeEntity.title.contains(searchNotice.getKeyword()))
                    );
                    break;
                case "writerId":
                    where = where.and(qNoticeEntity.modifiedBy.contains(searchNotice.getKeyword()));
                    break;
                case "title":
                    where = where.and(qNoticeEntity.title.contains(searchNotice.getKeyword()));
                    break;
                default:
                    break;
            }
        }

        // 정렬 방향 설정
        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.valueOf(searchNotice.getDirection().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            sortDirection = Sort.Direction.DESC;
        }

        // PageRequest 정의
        PageRequest pageRequest = PageRequest.of(
                searchNotice.getPage() - 1,
                searchNotice.getLimit(),
                Sort.by(
                        Sort.Order.desc("pinned"),
                        Sort.Order.desc("createdBy")
                )
        );

        // 데이터 조회
        Page<NoticeEntity> noticePage = noticeRepository.findAll(where, pageRequest);

        List<ListPageNotice> list = noticePage.getContent().stream()
                .map(noticeEntity -> ListPageNotice.builder()
                        .id(noticeEntity.getId())
                        .title(noticeEntity.getTitle())
                        .createdOn(DateTimeUtils.formatConditionalDateTime(noticeEntity.getCreatedOn()))
                        .createdBy(noticeEntity.getCreatedBy())
                        .view(noticeEntity.getView())
                        .build())
                .toList();

        return new PageImpl<>(list, noticePage.getPageable(), noticePage.getTotalElements());
    }

    @Transactional
    public GetNoticeResponse getNotice(String loginId, String noticeId) {
        NoticeEntity noticeEntity = noticeRepository.findById(noticeId)
                .orElseThrow(()-> new ApiException(ApiErrorCode.NOTICE_NOT_FOUND));
        UserEntity userEntity = userRepository.findById(loginId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));

        int view = noticeEntity.getView() + 1;

        noticeEntity.setView(view);

        List<NoticeFile> noticeFiles = noticeEntity.getNoticeFileEntities()
                .stream()
                .map(file -> NoticeFile.builder()
                        .noticeFileId(file.getId())
                        .originalFileName(file.getOriginalFileName())
                        .url(s3FileStorageService.getUrl(file.getFileName()))
                        .build()
                )
                .collect(Collectors.toList());

        return GetNoticeResponse.builder()
                .id(noticeId)
                .title(noticeEntity.getTitle())
                .content(noticeEntity.getContent())
                .view(view)
                .userRole(userEntity.getRole())
                .pinned(noticeEntity.isPinned())
                .files(noticeFiles)
                .build();
    }
    @Transactional
    public void updateNotice(String loginId, String noticeId, UpdateNoticeRequest updateNoticeRequest) {
        NoticeEntity noticeEntity = noticeRepository.findById(noticeId)
                .orElseThrow(()-> new ApiException(ApiErrorCode.NOTICE_NOT_FOUND));
        // 조건 : 관리자만 삭제 가능
        boolean isAdmin = userRepository.existsByIdAndRole(loginId, UserRole.ADMIN);
        if(!isAdmin){
            throw new ApiException(ApiErrorCode.ACCESS_DENIED);
        }
        if(ObjectUtils.isNotEmpty(updateNoticeRequest.getDeleteFileIds())){
            updateNoticeRequest.getDeleteFileIds().forEach( fileId -> {
                noticeEntity.getNoticeFileEntities().stream()
                        .filter( noticeFileEntity -> noticeFileEntity.getId().equals(fileId))
                        .findFirst()// 컬렉션 안에 fileEntity는 하나만 존재한다는 가정 하에 첫번째 하나만 처리
                        .ifPresent(noticeFileEntity -> {
                            String s3Key = noticeFileEntity.getFileName();
                            if(s3Key != null && !s3Key.isBlank()){
                                s3FileStorageService.delete(s3Key);
                            }
                        });

            });
        }
        // 새로 업로드 된 파일을 S3에 업로드
        List<FileMeta> uploadedFiles = s3FileStorageService.upload(updateNoticeRequest.getFiles(),"board/notice");

        // DB 제거
        List<NoticeFileEntity> noticeFileEntities = noticeEntity.getNoticeFileEntities().stream()
                .filter(file -> updateNoticeRequest.getDeleteFileIds() == null // 삭제할 파일 리스트가 없을때 -> 아무것도 삭제하지 않음
                                || !updateNoticeRequest.getDeleteFileIds().contains(file.getId())) // 삭제할 파일 리스트에 현재 파일의 식별키가 없으면 유지
                .toList();

        // 업로드된 파일들을 NoticeFileEntity로 변환해 추가
        List<NoticeFileEntity> newFileEntities = uploadedFiles.stream()
                .map(fileMeta -> NoticeFileEntity.builder()
                        .fileName(fileMeta.getS3Key())
                        .originalFileName(fileMeta.getOriginalFileName())
                        .modifiedBy(loginId)
                        .noticeEntity(noticeEntity)
                        .build()
                )
                .collect(Collectors.toList());

        // 컬렉션 재구성 ( 기존 + 추가 파일)
        noticeEntity.getNoticeFileEntities().clear(); // 기존 연결 끊기
        noticeEntity.getNoticeFileEntities().addAll(noticeFileEntities); // 기존 파일 추가
        noticeEntity.getNoticeFileEntities().addAll(newFileEntities); // 새로운 파일 추가

        // 공지사항 내용 수정
        noticeEntity.setTitle(updateNoticeRequest.getTitle());
        noticeEntity.setContent(updateNoticeRequest.getContent());
        noticeEntity.setPinned(updateNoticeRequest.isPinned());
        noticeEntity.setModifiedBy(loginId);

        noticeRepository.save(noticeEntity);
    }

    @Transactional
    public void deleteNotice(String loginId, String noticeId) {
        NoticeEntity noticeEntity = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOTICE_NOT_FOUND));

        List<NoticeFileEntity> noticeFileEntities = noticeFileRepository.findAllByNoticeEntity(noticeEntity);
        for (NoticeFileEntity file : noticeEntity.getNoticeFileEntities()) {
            s3FileStorageService.delete(file.getFileName());
        }

        noticeEntity.getNoticeFileEntities().clear();
        noticeRepository.deleteById(noticeId);

    }
}



