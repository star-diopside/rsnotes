package jp.gr.java_conf.stardiopside.rsnotes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestHandler;

@Configuration
public class WebFluxSecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(spec -> spec
                        .anyExchange().authenticated())
                .formLogin(Customizer.withDefaults())
                .csrf(spec -> spec
                        .csrfTokenRequestHandler(serverCsrfTokenRequestHandler()))
                .build();
    }

    private ServerCsrfTokenRequestHandler serverCsrfTokenRequestHandler() {
        var handler = new ServerCsrfTokenRequestAttributeHandler();
        handler.setTokenFromMultipartDataEnabled(true);
        return handler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
