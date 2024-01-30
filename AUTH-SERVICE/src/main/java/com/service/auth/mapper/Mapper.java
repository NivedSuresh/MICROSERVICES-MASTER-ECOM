package com.service.auth.mapper;

import com.service.auth.model.UserEntity;
import com.service.auth.payloads.SignupRequest;
import com.service.auth.payloads.UserDto;
import org.springframework.security.core.Authentication;

public interface Mapper {
    UserEntity signupRequestToEntity(SignupRequest request);
    UserDto entityToDto(UserEntity userEntity);

    UserDto authenticationToDto(Authentication authentication);
}
