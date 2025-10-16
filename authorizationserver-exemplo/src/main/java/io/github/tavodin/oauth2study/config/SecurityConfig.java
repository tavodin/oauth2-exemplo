package io.github.tavodin.oauth2study.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Configurações do Authoziation Server
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http, CustomInfoMapper userInfoMapper) throws Exception {

        // OAuth2AuthorizationServerConfigurer -> Habilita todos os endpoints padrões de autorização e autenticação.
        // .authorizationServer() -> Cria a configuração do servidor de autorização.
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                OAuth2AuthorizationServerConfigurer.authorizationServer();

        http
                // Adiciona as regras somente nos endpoints relacionados ao OAuth2/OIDC
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())

                .with(authorizationServerConfigurer, (authorizationServer) -> authorizationServer
                        // Adiciona o OIDC
                        .oidc(oidc -> oidc
                                .providerConfigurationEndpoint(configurer ->
                                        configurer.providerConfigurationCustomizer(providerConfig -> {
                                            // Adiciona os scopes no "/.well-known/openid-configuration"
                                            providerConfig.claim("scopes_supported",
                                                    List.of("openid", "profile", "email"));
                                        })
                                )

                                // Customiza o '/userinfo' com a classe CustomInfoMapper
                                .userInfoEndpoint(userInfo ->
                                        userInfo.userInfoMapper(userInfoMapper)
                                )
                        )
                )
                .authorizeHttpRequests((authorize) ->
                        authorize.anyRequest().authenticated())

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // configura como o spring security reage quando há uma exceção
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                // Redireciona para '/login'
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                // Restringe a regra para requisições que aceitam text/html
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                        .defaultAuthenticationEntryPointFor(
                                // Responde com UNAUTHORIZED
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                // Restringe a regra para requisições que aceitam json
                                new MediaTypeRequestMatcher(MediaType.APPLICATION_JSON)
                        )
                );

        return http.build();
    }

    // Configuração padrão
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().authenticated())
                .formLogin(Customizer.withDefaults());
        return http.build();
    }

    // Adiciona um Client em memória
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient = RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId("my-client")
                .clientSecret(encoder().encode("my-secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(30))
                        .refreshTokenTimeToLive(Duration.ofDays(7))
                        .reuseRefreshTokens(true)
                        .idTokenSignatureAlgorithm(SignatureAlgorithm.RS256)
                        .build())
                .redirectUri("https://oauth.pstmn.io/v1/callback")
                .postLogoutRedirectUri("http://localhost:8080")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope(OidcScopes.EMAIL)
                .build();

        RegisteredClient spaClient = RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId("my-client-spa")
                .clientSecret(encoder().encode("my-secret-spa"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(30))
                        .refreshTokenTimeToLive(Duration.ofDays(7))
                        .reuseRefreshTokens(true)
                        .idTokenSignatureAlgorithm(SignatureAlgorithm.RS256)
                        .build())
                .redirectUri("http://localhost:4200/callback")
                .postLogoutRedirectUri("http://localhost:4200")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope(OidcScopes.EMAIL)
                .build();

        return new InMemoryRegisteredClientRepository(registeredClient, spaClient);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // Adiciona o encoder, BCrypt por padrão
    @Bean
    public PasswordEncoder encoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
