package com.lms.api.admin.board.community;


import com.lms.api.admin.File.S3FileStorageService;
import com.lms.api.admin.File.dto.FileMeta;
import com.lms.api.admin.board.community.dto.CreateCommunityRequest;
import com.lms.api.admin.board.community.dto.ListCommunity;
import com.lms.api.admin.board.community.dto.SearchCommunity;
import com.lms.api.common.entity.board.*;
import com.lms.api.common.exception.ApiErrorCode;
import com.lms.api.common.exception.ApiException;
import com.lms.api.common.repository.board.CommunityRepository;
import com.lms.api.common.util.FileUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommunityService {
    private final S3FileStorageService s3FileStorageService;
    private final CommunityServiceMapper communityServiceMapper;
    private final CommunityRepository communityRepository;
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

        String id = "BC" +System.nanoTime();

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
}



