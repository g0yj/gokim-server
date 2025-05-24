package com.lms.api.admin.board.notice;


import com.lms.api.admin.File.S3FileStorageService;
import com.lms.api.admin.board.dto.CreateNotice;
import com.lms.api.admin.board.notice.dto.GetNoticeResponse;
import com.lms.api.admin.board.notice.dto.ListPageNoticeResponse;
import com.lms.api.admin.board.notice.dto.NoticeFile;
import com.lms.api.admin.board.notice.dto.SearchNotice;
import com.lms.api.admin.project.file.dto.FileMeta;
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
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


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
    public Page<ListPageNoticeResponse> pageListNotice(String loginId, SearchNotice searchNotice) {
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
                    where = where.and(
                            qNoticeEntity.modifiedBy.contains(searchNotice.getKeyword())
                    );
                    break;
                case "title":
                    where = where.and(
                            qNoticeEntity.title.contains(searchNotice.getKeyword())
                    );
                    break;
                default:
                    break;
            }
        }

        // 페이징 관련 데이터 정의
        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.valueOf(searchNotice.getDirection().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            sortDirection = Sort.Direction.DESC;
        }
        // 조건이 있기 때문에 스프링 jpa가 제공하는 객체로 새롭게 조건 만듦.
        org.springframework.data.domain.PageRequest pageRequest = org.springframework.data.domain.PageRequest.of(
                searchNotice.getPage() - 1,
                searchNotice.getLimit(),
                Sort.by(
                        Sort.Order.desc("pinned"), // true 일때 상단 위치 (desc)
                        Sort.Order.desc("createdBy")
                )
        );
        // 페이징 처리를 포함한 데이터 만들기
        Page<NoticeEntity> noticePage = noticeRepository.findAll(where, pageRequest);

        // 프론트가 원하는 리스트 만듦
        return noticePage.map( notice -> {
            String writerId = notice.getModifiedBy();
            String writerName = userRepository.findById(writerId)
                    .map(UserEntity ::getName)
                    .orElse("-");
            return ListPageNoticeResponse.builder()
                    .id(notice.getId())
                    .title(notice.getTitle())
                    .createDate(notice.getCreatedOn().toLocalDate())
                    .writerId(writerId)
                    .writerName(writerName)
                    .fileCount(notice.getNoticeFileEntities() != null ? notice.getNoticeFileEntities().size() : 0)
                    .build();
        });
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
}



