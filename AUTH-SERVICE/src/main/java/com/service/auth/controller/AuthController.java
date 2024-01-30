package com.service.auth.controller;

import com.service.auth.JWT.JwtUtil;
import com.service.auth.payloads.AuthenticationResponse;
import com.service.auth.payloads.SignInRequest;
import com.service.auth.payloads.SignupRequest;
import com.service.auth.payloads.UserDto;
import com.service.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @InitBinder
    public void removeWhiteSpaces(WebDataBinder dataBinder){
        StringTrimmerEditor ste = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, ste);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/logout-success")
    public void filler(){}

    @PostMapping("/login")
    public AuthenticationResponse login(@Valid @RequestBody SignInRequest signInRequest){
        return authService.login(signInRequest);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public AuthenticationResponse signupUser(@Valid @RequestBody SignupRequest signupRequest, BindingResult result){
        if(result.hasErrors()) authService.handleFieldError(result);
        AuthenticationResponse authenticationResponse = authService.save(signupRequest);
        log.info("Authentication Response after Signing Up : {}",authenticationResponse);
        return authenticationResponse;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/get/token")
    public String jwtToken(Authentication authentication){
        return jwtUtil.getJwtToken(authentication);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping("/validate")
    public boolean validateToken(@AuthenticationPrincipal Jwt principal){
        log.debug("Authentication : {}", Optional.ofNullable(principal.getClaim("authority")));
        log.debug("Authentication : {}", principal.getSubject());
        return true;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/debug")
    public void debug(Authentication authentication){
        log.debug("Authentication Object : {}", authentication);
    }
}
