package com.lms.api.admin.batch.step.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter@Setter@AllArgsConstructor@Builder@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailSendInfo {
    String userId;
    String email;
    String subject;
    String body;
}
