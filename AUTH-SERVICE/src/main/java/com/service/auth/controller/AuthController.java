package com.service.auth.controller;

import com.service.auth.exceptions.Error;
import com.service.auth.exceptions.UserException;
import com.service.auth.payloads.AuthenticationResponse;
import com.service.auth.payloads.ErrorResponse;
import com.service.auth.payloads.SignInRequest;
import com.service.auth.payloads.SignupRequest;
import com.service.auth.service.AuthService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {


    private final AuthService authService;
    private final io.github.resilience4j.circuitbreaker.CircuitBreaker circuitBreaker;

    @InitBinder
    public void removeWhiteSpaces(WebDataBinder dataBinder){
        StringTrimmerEditor ste = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, ste);
    }


    @CircuitBreaker(name = "authentication", fallbackMethod = "loginFallback")
    @Retry(name = "authentication")
    @TimeLimiter(name = "authentication")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public Mono<AuthenticationResponse> login(@Valid @RequestBody SignInRequest signInRequest){
        return authService.login(signInRequest)
                .transform(CircuitBreakerOperator.of(circuitBreaker));
    }


    @CircuitBreaker(name = "authentication", fallbackMethod = "loginFallback")
    @Retry(name = "authentication")
    @TimeLimiter(name = "authentication")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public Mono<AuthenticationResponse> signupUser(@Valid @RequestBody SignupRequest signupRequest, BindingResult result){
        if(result.hasErrors()) authService.handleFieldError(result);
        return authService.save(signupRequest).transform(CircuitBreakerOperator.of(circuitBreaker));
    }

    public Mono<ResponseEntity<ErrorResponse>> loginFallback(SignupRequest signupRequest, BindingResult result, Exception e){
        return this.loginFallback(null, e);
    }

    public Mono<ResponseEntity<ErrorResponse>> loginFallback(WebDataBinder dataBinder, Exception e){
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .errorCode(Error.DATABASE_COMMUNICATION_FAILURE)
                        .message("This service is unavailable at the moment, please try after sometime!")
                        .build()
                )
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/debug")
    public void debug(Authentication authentication, @AuthenticationPrincipal Jwt principal){
        log.info("Principal : {}", principal);
        log.info("Authentication : {}", authentication);
        log.info("Authentication : {}", Optional.ofNullable(principal.getClaim("authority")));
        log.info("Authentication : {}", principal.getSubject());
    }
}
