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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
    public Mono<AuthenticationResponse> save(SignupRequest signupRequest) {

        if(!Objects.equals(signupRequest.getPassword(), signupRequest.getConfirmPassword()))
            throw new PasswordMissMatchException(
                    "The password field should match with the confirm password!",
                    HttpStatus.BAD_REQUEST.value(),
                    Error.SIGNUP_REQUEST_PASSWORD_MISMATCH
            );

        return Mono.just(userExistsByEmail(signupRequest.getEmail()))

                .zipWith(Mono.just(mapper.signupRequestToEntity(signupRequest)))

                .publishOn(Schedulers.boundedElastic())
                .handle((tuple, sink) -> {
                    if (tuple.getT1()) sink.error(throwUserAlreadyExists());
                    sink.next(saveToDb(tuple.getT2()));
                })

                .zipWhen(o -> {
                    UserEntity entity = (UserEntity) o;
                    return Mono.zip(
                            Mono.just(mapper.entityToDto(entity)),
                            Mono.just(generateAuthenticationToken(entity))
                    );
                })

                .map(objects -> {
                    Authentication authentication = objects.getT2().getT2();
                    UserDto userDto = objects.getT2().getT1();
                    return generateAuthenticationResponse(authentication, userDto);
                });
    }

    private Throwable throwUserAlreadyExists() {
        throw new UserAlreadyExistsException(
                "An account has already been registered with the provided Mail ID!",
                HttpStatus.NOT_ACCEPTABLE.value(),
                Error.USER_ALREADY_EXISTS
        );
    }

    private boolean userExistsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    private AuthenticationResponse generateAuthenticationResponse(Authentication t2, UserDto t1) {
        return new AuthenticationResponse(jwtUtil.getJwtToken(t2), t1);
    }

    private UserEntity saveToDb(UserEntity entity) {
        return userRepo.save(entity);
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
    public Authentication generateAuthenticationToken(UserEntity entity) {
        List<GrantedAuthority> grantedAuthorities =
                List.of(new SimpleGrantedAuthority(entity.getRole()));
        return new UsernamePasswordAuthenticationToken(entity.getEmail(), null,grantedAuthorities);
    }
}
