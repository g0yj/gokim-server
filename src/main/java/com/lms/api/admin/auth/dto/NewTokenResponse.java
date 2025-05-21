package com.lms.api.admin.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "리프레쉬 사용", name = "NewTokenResponse")
public class NewTokenResponse {
    String accessToken;
}
