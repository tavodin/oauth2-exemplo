package io.github.tavodin.oauth2study.config;

import io.github.tavodin.oauth2study.entities.User;
import io.github.tavodin.oauth2study.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Component
public class CustomInfoMapper implements Function<OidcUserInfoAuthenticationContext, OidcUserInfo> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OidcUserInfo apply(OidcUserInfoAuthenticationContext context) {
        OAuth2Authorization authorization = context.getAuthorization();
        Authentication principal = authorization.getAttribute(Principal.class.getName());

        User user = userRepository.searchByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found!"));

        Set<String> scopes = authorization.getAccessToken().getToken().getScopes();

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", user.getId());
        claims.put("name", user.getName());
        claims.put("email", user.getEmail());
        claims.put("phone", user.getPhone());
        claims.put("gender", user.getGender());

        return new OidcUserInfo(claims);
    }
}
