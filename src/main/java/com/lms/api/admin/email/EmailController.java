package com.lms.api.admin.email;

import com.lms.api.admin.auth.LoginUser;
import com.lms.api.admin.email.dto.SendEmailRequest;
import com.lms.api.admin.project.ProjectControllerMapper;
import com.lms.api.admin.project.ProjectService;
import com.lms.api.admin.project.dto.*;
import com.lms.api.common.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/email")
@Tag(name = "Email API", description = "이메일 전송 관련 API입니다")
@RequiredArgsConstructor
@Slf4j
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<?> send(@ModelAttribute SendEmailRequest sendEmailRequest) {
        emailService.send(sendEmailRequest);
        return ResponseEntity.ok("ok");
    }


}
