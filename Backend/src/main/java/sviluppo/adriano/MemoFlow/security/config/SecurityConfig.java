package sviluppo.adriano.MemoFlow.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
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
                        .requestMatchers("/auth/login**").permitAll()

                       .requestMatchers("/utente/**").permitAll()

                        .requestMatchers("/auth/**").permitAll()

                        .requestMatchers("/credenziali/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/nota/listaTutte").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/nota/eliminaNota/{id}").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/nota/aggiornaNota/{id}").permitAll() // Se hai anche PUT/aggiornamento
                        .requestMatchers(HttpMethod.POST, "/nota/creaNota").permitAll()
                        .requestMatchers(HttpMethod.GET, "/nota/perDataEUtente").permitAll()
                        .requestMatchers(HttpMethod.GET, "/nota/{id}").permitAll()

                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}