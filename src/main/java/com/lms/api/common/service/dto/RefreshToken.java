package com.lms.api.common.service.dto;

import jakarta.persistence.Id;
import lombok.*;

/**
 * redis 저장을 위해 필요 DTO
 */
@Getter
@Setter
public class RefreshToken {

    @Id
    private String userId;  // key : 사용자 id (또는 username)

    private String token;   // value : 발급한 RefreshToken
}
