package com.lms.api.admin.project.task.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter@Setter
@AllArgsConstructor@NoArgsConstructor@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateTask {

    String id;

    String title;
    String description;
    String assignedMember;
    long taskStatusId;


    List<MultipartFile> multipartFiles; // 파일들
    List<Long> deleteFiles; // 삭제할 파일 목록
    String fileName;

    String modifiedBy;
}
