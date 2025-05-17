package com.lms.api.admin.File;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * local 은 DB를 사용 하고 , dev는 S3을 사용하기 위해
 */
public interface FileStorageService {
    String upload(MultipartFile file, String filename);
    Resource download(String filename);
}
