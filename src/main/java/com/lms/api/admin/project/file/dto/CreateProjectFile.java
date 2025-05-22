package com.lms.api.admin.project.file.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter@Getter@AllArgsConstructor@NoArgsConstructor@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateProjectFile {
    List<MultipartFile> multipartFiles;
    String projectFunctionId;
    String createdBy;

}
