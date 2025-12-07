package learn.auth;

import lombok.Data;

@Data
public class LoginResponse {
    private final String token;
    private final String username;
    private final String fullname;
    private final String phoneNumber;
    private final String role;
}