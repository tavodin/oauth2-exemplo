package io.github.tavodin.oauth2study.config;

import io.github.tavodin.oauth2study.entities.User;
import io.github.tavodin.oauth2study.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class TokenCustomizerConfig {

    @Autowired
    private UserRepository repository;

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            Authentication principal = context.getPrincipal();

            if(context.getTokenType().getValue().equals("access_token")) {
                Set<String> authorities = principal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet());

                context.getClaims().claim("roles", authorities);
            }

            if(context.getTokenType().getValue().equals("id_token")) {
                String email = principal.getName();
                Set<String> scopes = context.getAuthorizedScopes();

                User user = repository.searchByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("Username not found!"));

                context.getClaims().claim("sub", user.getId());

                if(scopes.contains(OidcScopes.EMAIL)) {
                    context.getClaims().claim(OidcScopes.EMAIL, user.getEmail());
                }

                if(scopes.contains(OidcScopes.PROFILE)) {
                    context.getClaims().claim(OidcScopes.PROFILE, user.getName());
                }

                if(scopes.contains(OidcScopes.PHONE)) {
                    context.getClaims().claim(OidcScopes.PHONE, user.getPhone());
                }
            }
        };
    }
}
