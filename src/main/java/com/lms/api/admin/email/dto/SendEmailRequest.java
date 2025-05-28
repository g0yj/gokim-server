package com.lms.api.admin.email.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter@Setter@Builder
@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendEmailRequest {
    String userId;
    String email;
    String subject;
    String body;
    List<MultipartFile> files;
    List<String> linkFiles;
}
