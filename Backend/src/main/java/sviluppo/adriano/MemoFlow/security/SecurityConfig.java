package sviluppo.adriano.MemoFlow.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {  // Cambia il nome della classe per evitare conflitti

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",          // interfaccia visiva
                                "/v3/api-docs/**",         // dati swagger json
                                "/v3/api-docs",            // root dei docs
                                "/swagger-resources/**",   // risorse swagger
                                "/configuration/**",       // configurazioni legacy
                                "/webjars/**",             // dipendenze js/css
                                "/utente/crea"             // la tua chiamata pubblica
                        ).permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }

}
