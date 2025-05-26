package com.lms.api.admin.File;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;

/**
 * Presigned URL을 생성하는 커스텀 컴포넌트
 */
@Component
@RequiredArgsConstructor
public class S3PresignedUrlProvider   {
    // AWS SDK에서 제공하는 Presigned URL 생성 도구 (@Bean 등록 필요. config 에 정의)
    private final S3Presigner s3Presigner;

    public String createDownloadLink(String bucketName, String key, Duration duration) {
        // Presigned URL에 대한 요청 결과. 최종적으로 .url()로 URL을 꺼냄
        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(
                // 어떤 S3 객체의 URL을 만들지, 얼마나 유효할지 등을 담은 요청 객체
                GetObjectPresignRequest.builder()
                        .signatureDuration(duration)
                        .getObjectRequest(req -> req.bucket(bucketName).key(key))
                        .build()
        );
        return presignedGetObjectRequest.url().toString();
    }
}
