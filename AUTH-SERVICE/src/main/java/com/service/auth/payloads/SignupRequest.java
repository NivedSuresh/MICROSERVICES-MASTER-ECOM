package com.service.auth.payloads;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @Email(message = "Enter a valid email")
    private String email;
    @Size(min = 5, max = 20, message = "Username should be of 5-20 characters!")
    private String username;
    @Size(min = 4, max = 20, message = "Password should be of 4-20 characters!")
    private String password;
    @Size(min = 4, max = 20, message = "Confirm Password should be of 4-20 characters!")
    private String confirmPassword;
}
