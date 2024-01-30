package com.service.auth.service;

import com.service.auth.model.UserEntity;
import com.service.auth.payloads.AuthenticationResponse;
import com.service.auth.payloads.SignInRequest;
import com.service.auth.payloads.SignupRequest;
import com.service.auth.payloads.UserDto;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;

public interface AuthService {
    AuthenticationResponse save(SignupRequest signupRequest);
    UserEntity findByEmail(String email);

    void handleFieldError(BindingResult result);

    Authentication generateAuthenticationToken(UserDto userDto);

    AuthenticationResponse login(SignInRequest signInRequest);
}
