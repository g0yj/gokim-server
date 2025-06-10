package com.lms.api.admin.board.anon.dto;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter@Setter@AllArgsConstructor@NoArgsConstructor@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateAnonBoard {
    String title;
    String content;
    List<MultipartFile> multipartFiles;
    String createdBy;

}
