package com.lms.api.admin.batch.step.dto;

import com.lms.api.admin.File.dto.OldFileInfo;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter@Setter@AllArgsConstructor@Builder@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserOldFiles {
    String userId;
    List<OldFileInfo> oldFiles;
}
