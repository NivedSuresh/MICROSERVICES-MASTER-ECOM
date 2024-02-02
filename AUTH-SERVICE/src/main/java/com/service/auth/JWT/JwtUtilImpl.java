package com.service.auth.JWT;

import com.service.auth.payloads.UserDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class JwtUtilImpl implements JwtUtil{

    private final JwtEncoder jwtEncoder;

    @Override
    public String getJwtToken(UserDto userDto) {
        Instant now = Instant.now();
//        String authority = authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));

        log.info("Authority from getJwtToken method : {}" , userDto.getRole());

        JwtClaimsSet claimsSet = JwtClaimsSet.builder().issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.DAYS))
                .issuer("self")
                .subject(userDto.getEmail())
                .claim("authority", userDto.getRole())
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }

}
