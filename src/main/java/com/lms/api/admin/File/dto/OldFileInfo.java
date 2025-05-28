package com.lms.api.admin.File.dto;

import com.lms.api.admin.File.enums.FileTableType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


@AllArgsConstructor@NoArgsConstructor@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OldFileInfo {
    String userId;
    String s3Key;
    String originalFileName;
    FileTableType fileTableType; // 실제 삭제할 DB 식별키
}
