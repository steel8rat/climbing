package com.mokhov.climbing.config;

import com.mokhov.climbing.exceptions.JwtTokenMalformedException;
import com.mokhov.climbing.models.JwtAuthenticatedUser;
import com.mokhov.climbing.models.JwtAuthenticationToken;
import com.mokhov.climbing.models.JwtParsedUser;
import com.mokhov.climbing.services.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class JwtAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final JwtService jwtService;

    public JwtAuthenticationProvider(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Override
    protected void additionalAuthenticationChecks(final UserDetails d, final UsernamePasswordAuthenticationToken auth) {
        // Nothing to do
    }

    @Override
    protected JwtAuthenticatedUser retrieveUser(final String username, final UsernamePasswordAuthenticationToken authentication) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String token = jwtAuthenticationToken.getToken();
        JwtParsedUser parsedUser = jwtService.parseToken(token);
        if (parsedUser == null) throw new JwtTokenMalformedException("JWT token is not valid");
        List<GrantedAuthority> authorityList = AuthorityUtils.commaSeparatedStringToAuthorityList(parsedUser.getRoles());
        return new JwtAuthenticatedUser(parsedUser.getId(), parsedUser.getNickname(), authorityList);
    }
}
