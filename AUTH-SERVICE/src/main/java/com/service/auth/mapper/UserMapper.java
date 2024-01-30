package com.service.auth.mapper;

import com.service.auth.model.UserEntity;
import com.service.auth.payloads.SignupRequest;
import com.service.auth.payloads.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMapper implements Mapper{

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserEntity signupRequestToEntity(SignupRequest request) {
        return UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();
    }

    @Override
    public UserDto entityToDto(UserEntity userEntity) {
        return UserDto.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .role(userEntity.getRole())
                .username(userEntity.getUsername())
                .build();
    }

    @Override
    public UserDto authenticationToDto(Authentication authentication) {
        String role = authentication.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .toList().get(0);
        return UserDto.builder()
                .email(authentication.getName())
                .role(role)
                .build();
    }
}
