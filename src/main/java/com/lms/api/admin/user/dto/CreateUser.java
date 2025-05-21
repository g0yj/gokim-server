package com.lms.api.admin.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter@Setter
@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "CreateUser", description = "회원가입")
public class CreateUser {

    String id;
    String password;
    String name;
    String phone;
    String email;

    MultipartFile multipartFile;
    String fileName;
}
