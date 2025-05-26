package com.lms.api.admin.board.notice.dto;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter@Setter@AllArgsConstructor@NoArgsConstructor@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateNotice {
    String title;
    String content;
    boolean pinned;
    List<MultipartFile> multipartFiles;
    String createdBy;

}
