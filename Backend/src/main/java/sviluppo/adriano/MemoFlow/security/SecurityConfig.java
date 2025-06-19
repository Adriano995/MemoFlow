package sviluppo.adriano.MemoFlow.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/swagger-resources/**",
                                "/configuration/**",
                                "/webjars/**",
                                "/favicon.ico",
                                "/utente/crea"
                        ).permitAll()

                        //Da bloccare in futuro con filtro JWT
                       .requestMatchers("/utente/utenti").permitAll()

                        .requestMatchers("/utente/aggiorna/{id}").permitAll()

                        .requestMatchers("/utente/eliminaUtente/{id}").permitAll()

                        .requestMatchers("/auth/**").permitAll()

                        .requestMatchers("/credenziali/cambiaPassword/**").permitAll()

                        .requestMatchers("/credenziali/cambiaEmail/**").permitAll()

                        .anyRequest().authenticated()
                );

        return http.build();
    }
}