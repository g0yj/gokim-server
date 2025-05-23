package com.lms.api.admin.File.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter@Setter@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileMeta {
    String originalFileName;
    String s3Key;
    String url;
}
