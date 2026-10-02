package framework.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class AdminAuthenticationToken extends UsernamePasswordAuthenticationToken {
    public AdminAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }
}
