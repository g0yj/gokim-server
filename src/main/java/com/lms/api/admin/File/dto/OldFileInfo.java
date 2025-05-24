package com.lms.api.admin.File.dto;

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

}
