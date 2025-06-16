package com.lms.api.common.util;

import com.lms.api.admin.user.enums.UserRole;
import com.lms.api.common.entity.BaseEntity;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.exception.ApiErrorCode;
import com.lms.api.common.exception.ApiException;
import com.lms.api.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthUtils {
    private final UserRepository userRepository;

    public boolean isOwner(String loginId, BaseEntity baseEntity) {
        return Objects.equals(baseEntity.getCreatedBy(), loginId);
    }

    public boolean isAdmin(String loginId) {
        if (loginId == null) return false;
        return userRepository.findById(loginId)
                .map(user -> user.getRole().equals(UserRole.ADMIN))
                .orElse(false);
    }

    public void validateOwnerOrAdmin(String loginId, BaseEntity baseEntity) {
        if (!isOwner(loginId, baseEntity) && !isAdmin(loginId)) {
            throw new ApiException(ApiErrorCode.ACCESS_DENIED);
        }
    }

    public void validateOwner(String loginId, BaseEntity baseEntity){
        if(!isOwner(loginId,baseEntity)){
            throw new ApiException(ApiErrorCode.ACCESS_DENIED);
        }
    }

    public void validateAdmin(String loginId){
        if(!isAdmin(loginId)){
            throw new ApiException(ApiErrorCode.ACCESS_DENIED);
        }
    }
}
