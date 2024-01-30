package com.service.auth.service.impls;

import com.service.auth.JWT.JwtUtil;
import com.service.auth.exceptions.*;
import com.service.auth.exceptions.Error;
import com.service.auth.mapper.Mapper;
import com.service.auth.model.UserEntity;
import com.service.auth.payloads.AuthenticationResponse;
import com.service.auth.payloads.SignInRequest;
import com.service.auth.payloads.SignupRequest;
import com.service.auth.payloads.UserDto;
import com.service.auth.repo.UserRepo;
import com.service.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final Mapper mapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public AuthenticationResponse save(SignupRequest signupRequest) {

        if(!Objects.equals(signupRequest.getPassword(), signupRequest.getConfirmPassword()))
            throw new PasswordMissMatchException(
                    "The password field should match with the confirm password!",
                    HttpStatus.BAD_REQUEST.value(),
                    Error.SIGNUP_REQUEST_PASSWORD_MISMATCH
            );

        if(userRepo.existsByEmail(signupRequest.getEmail()))
            throw new UserAlreadyExistsException(
                    "An account has already been registered with the provided Mail ID!",
                    HttpStatus.NOT_ACCEPTABLE.value(),
                    Error.USER_ALREADY_EXISTS
            );

        UserEntity userEntity = mapper.signupRequestToEntity(signupRequest);
        userEntity = userRepo.save(userEntity);
        UserDto userDto = mapper.entityToDto(userEntity);
        Authentication authentication = generateAuthenticationToken(userDto);

        log.info("DTO : "+userDto);
        log.info("Entity : "+userEntity);
        log.info("Authentication : "+authentication);

        return new AuthenticationResponse(jwtUtil.getJwtToken(authentication), userDto);
    }

    /*
        Method will only be used for fetching the entity for user
        authentication, thus converting to response is not mandatory.
        Null check will be done from UserDetailsService implementation
    */
    @Override
    public UserEntity findByEmail(String email) {
        try{
            return userRepo.findByEmail(email);
        }catch (RuntimeException e){
            log.error("Unable to initiate communication with the database : {}", e.getMessage());
            throw new UserException(
                    "Unable to complete the operation at this point of time",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    Error.DATABASE_COMMUNICATION_FAILURE
            );
        }
    }


    @Override
    public AuthenticationResponse login(SignInRequest signInRequest) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword())
            );
            UserDto userDto = mapper.authenticationToDto(authentication);
            return new AuthenticationResponse(jwtUtil.getJwtToken(authentication), userDto);
        }catch (Exception e){
            throw new InvalidCredentialsException(
                    "Invalid credentials provided",
                    HttpStatus.FORBIDDEN.value(),
                    Error.INVALID_CREDENTIALS_PROVIDED
            );
        }
    }


    @Override
    public void handleFieldError(BindingResult result) {
        String message = result.getAllErrors().get(0).getDefaultMessage();
        throw new UserException(message, HttpStatus.BAD_REQUEST.value(), Error.SIGNUP_REQUEST_INVALID_FIELDS);
    }

    @Override
    public Authentication generateAuthenticationToken(UserDto userDto) {
        List<GrantedAuthority> grantedAuthorities =
                List.of(new SimpleGrantedAuthority(userDto.getRole()));
        return new UsernamePasswordAuthenticationToken(userDto.getEmail(), null,grantedAuthorities);
    }
}
