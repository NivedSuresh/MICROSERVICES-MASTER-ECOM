package com.service.auth.JWT;

import com.service.auth.payloads.UserDto;
import org.springframework.security.core.Authentication;

public interface JwtUtil {
    String getJwtToken(Authentication authentication);
}
