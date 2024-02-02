package com.service.auth.service;

import com.service.auth.model.UserEntity;
import com.service.auth.payloads.AuthenticationResponse;
import com.service.auth.payloads.SignInRequest;
import com.service.auth.payloads.SignupRequest;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<AuthenticationResponse> save(SignupRequest signupRequest);
    UserEntity findByEmail(String email);

    void handleFieldError(BindingResult result);

    Authentication generateAuthenticationToken(SignInRequest request);

    Mono<AuthenticationResponse> login(SignInRequest signInRequest);
}
