package framework.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class EmpAuthenticationToken extends UsernamePasswordAuthenticationToken {
    public EmpAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }
}
