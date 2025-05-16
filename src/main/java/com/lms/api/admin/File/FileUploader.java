package com.lms.api.admin.File;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileUploader {
    String upload(MultipartFile file, String dirName) throws IOException;

}
