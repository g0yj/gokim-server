package com.lms.api.common.util;

import com.lms.api.admin.user.enums.UserRole;
import com.lms.api.common.entity.BaseEntity;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.exception.ApiErrorCode;
import com.lms.api.common.exception.ApiException;
import com.lms.api.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthUtils {
    private final UserRepository userRepository;

    public boolean isOwner(String loginId, BaseEntity baseEntity) {
        return baseEntity.getCreatedBy().equals(loginId);
    }

    public boolean isAdmin(String loginId) {
        return userRepository.findById(loginId)
                .map(user -> user.getRole().equals(UserRole.ADMIN))
                .orElse(false);
    }

    public void validateOwnerOrAdmin(String loginId, BaseEntity baseEntity) {
        if (!isOwner(loginId, baseEntity) && !isAdmin(loginId)) {
            throw new ApiException(ApiErrorCode.ACCESS_DENIED);
        }
    }
}
