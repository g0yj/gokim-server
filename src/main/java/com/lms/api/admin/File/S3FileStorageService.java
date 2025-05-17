package com.lms.api.admin.File;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

@Service
@Profile("dev")
@RequiredArgsConstructor
public class S3FileStorageService implements FileStorageService{

    private final S3Client s3Client;
    @Override
    public String upload(MultipartFile file, String filename) {
        return null;
    }

    @Override
    public Resource download(String filename) {
        return null;
    }
}
