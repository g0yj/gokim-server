package com.lms.api.admin.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter@Setter
@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUser {
    String password;
    String name;
    String phone;
    String email;
    MultipartFile multipartFile;
    String fileName;
    String modifiedBy;
}
