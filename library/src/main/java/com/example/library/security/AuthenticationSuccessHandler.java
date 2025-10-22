package com.example.library.security;

import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public AuthenticationSuccessHandler() {
        super();
        setDefaultTargetUrl("/home");
        setAlwaysUseDefaultTargetUrl(true);
    }
}