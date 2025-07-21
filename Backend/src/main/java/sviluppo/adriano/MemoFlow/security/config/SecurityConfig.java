/*package sviluppo.adriano.MemoFlow.security.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import sviluppo.adriano.MemoFlow.repository.AuthorityRepository;
import sviluppo.adriano.MemoFlow.security.jwt.JwtAuthFilter;
import sviluppo.adriano.MemoFlow.security.jwt.JwtAuthenticationEntryPoint; 
import sviluppo.adriano.MemoFlow.security.service.UserDetailServiceImpl;

@Configuration
@EnableMethodSecurity(prePostEnabled = true) 
public class SecurityConfig {

    private final UserDetailServiceImpl userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final AuthorityRepository authorityRepository;

    public SecurityConfig(UserDetailServiceImpl userDetailsService, JwtAuthFilter jwtAuthFilter, JwtAuthenticationEntryPoint unauthorizedHandler, AuthorityRepository authorityRepository) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
        this.unauthorizedHandler = unauthorizedHandler;
        this.authorityRepository = authorityRepository;
    }

    /**
     * Definisce il bean PasswordEncoder.
     * Usa BCryptPasswordEncoder per hashare le password.
     * @return L'implementazione di PasswordEncoder.
     */
/*    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Definisce il provider di autenticazione.
     * Utilizza il CustomUserDetailsService per caricare i dettagli dell'utente
     * e il PasswordEncoder per verificare le password.
     * @return Il DaoAuthenticationProvider configurato.
     */
/*    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());  
        return authProvider;
    }

    /**
     * Definisce il bean AuthenticationManager.
     * Viene usato nel AuthController per autenticare l'utente.
     * @param authConfig L'oggetto di configurazione dell'autenticazione.
     * @return L'AuthenticationManager.
     * @throws Exception Se si verifica un errore durante la configurazione.
     */
/*    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Configura la catena di filtri di sicurezza HTTP.
     * Questo è il metodo principale per definire le regole di sicurezza.
     * @param http L'oggetto HttpSecurity per configurare la sicurezza.
     * @return La SecurityFilterChain configurata.
     * @throws Exception Se si verifica un errore durante la configurazione.
     */
/*    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/swagger-resources/**",
                                "/configuration/**",
                                "/webjars/**",
                                "/favicon.ico"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/utente/creaUtente").permitAll()
                        .requestMatchers(HttpMethod.GET,"/utente/listaUtenti").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/utente/aggiornaDati/{id}").hasAnyRole("DEVELOPER", "USER")
                        .requestMatchers(HttpMethod.PUT, "/utente/cambiaRuoli/{id}").permitAll() //.hasAnyRole("DEVELOPER", "PROPRIETARIO_GRUPPO")
                        .requestMatchers(HttpMethod.GET, "/nota/listaTutte").hasAnyRole("DEVELOPER", "PROPRIETARIO_GRUPPO")
                        .requestMatchers(HttpMethod.GET, "/nota/{id}").hasAnyRole("DEVELOPER", "USER")
                        .requestMatchers(HttpMethod.GET, "/nota/perDataEUtente").hasAnyRole("DEVELOPER", "USER")
                        .requestMatchers(HttpMethod.GET, "/nota/perUtente").hasAnyRole("DEVELOPER", "USER")
                        .requestMatchers("/nota/**").hasAnyRole("DEVELOPER", "USER")
                        .requestMatchers("/eventi/**").hasAnyRole("DEVELOPER", "USER")
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                        .anyRequest().authenticated()
                );
                

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configurazione CORS per permettere richieste da domini diversi.
     * Questo bean è essenziale se il tuo frontend (Angular) e backend girano su porte diverse.
     * @return Il CorsConfigurationSource.
     */
/*    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //configuration.setAllowedOrigins(List.of("http://localhost:4200", "http://192.168.1.180:4200"));
        configuration.addAllowedOriginPattern("*");
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}*/


package sviluppo.adriano.MemoFlow.security.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher; // Aggiungi questo import
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource; // <-- DEVE ESSERE QUESTO
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // <-- E QUESTO

import sviluppo.adriano.MemoFlow.repository.AuthorityRepository;
import sviluppo.adriano.MemoFlow.security.jwt.JwtAuthFilter;
import sviluppo.adriano.MemoFlow.security.jwt.JwtAuthenticationEntryPoint;
import sviluppo.adriano.MemoFlow.security.service.UserDetailServiceImpl;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserDetailServiceImpl userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final AuthorityRepository authorityRepository;

    public SecurityConfig(UserDetailServiceImpl userDetailsService, JwtAuthFilter jwtAuthFilter, JwtAuthenticationEntryPoint unauthorizedHandler, AuthorityRepository authorityRepository) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
        this.unauthorizedHandler = unauthorizedHandler;
        this.authorityRepository = authorityRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource((org.springframework.web.cors.CorsConfigurationSource) corsConfigurationSource()))
                // Il tuo .csrf(AbstractHttpConfigurer::disable) è già globale e quindi sufficiente per H2.
                // Se volessi abilitare CSRF in futuro ma disabilitarlo solo per H2, useresti:
                // .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")))
                .csrf(AbstractHttpConfigurer::disable) // Mantenuto il tuo disable globale
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/swagger-resources/**",
                                "/configuration/**",
                                "/webjars/**",
                                "/favicon.ico"
                        ).permitAll()
                        // Rimuovi questo se l'hai messo due volte, una basta
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll() // Assicurati che questo sia qui e UNICO
                        .requestMatchers(HttpMethod.POST, "/utente/creaUtente").permitAll()
                        .requestMatchers(HttpMethod.GET,"/utente/listaUtenti").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/utente/aggiornaDati/{id}").hasAnyRole("DEVELOPER", "USER")
                        .requestMatchers(HttpMethod.PUT, "/utente/cambiaRuoli/{id}").permitAll() //.hasAnyRole("DEVELOPER", "PROPRIETARIO_GRUPPO")
                        .requestMatchers(HttpMethod.GET, "/nota/listaTutte").hasAnyRole("DEVELOPER", "USER")
                        .requestMatchers(HttpMethod.GET, "/nota/{id}").hasAnyRole("DEVELOPER", "USER")
                        .requestMatchers(HttpMethod.GET, "/nota/perDataEUtente").hasAnyRole("DEVELOPER", "USER")
                        .requestMatchers(HttpMethod.GET, "/nota/perUtente").hasAnyRole("DEVELOPER", "USER")
                        .requestMatchers("/nota/**").hasAnyRole("DEVELOPER", "USER")
                        .requestMatchers("/eventi/**").hasAnyRole("DEVELOPER", "USER")
                        .anyRequest().authenticated()
                )
                // Aggiungi questa parte per disabilitare X-Frame-Options per H2 Console
                .headers(headers -> headers
                    .frameOptions(frameOptions -> frameOptions
                        .disable() // Disabilita l'header X-Frame-Options per tutti
                        // Se volessi disabilitarlo solo per H2 console e mantenerlo per il resto:
                        // .sameOrigin() // Imposta X-Frame-Options a SAMEORIGIN (comportamento predefinito senza .disable())
                        // Oppure più specifico per H2
                        // .addHeaderWriter(new XFrameOptionsHeaderWriter(new WhiteListedAllowFromStrategy(Arrays.asList("http://localhost:8080"))))
                    )
                );


        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //configuration.setAllowedOrigins(List.of("http://localhost:4200", "http://192.168.1.180:4200"));
        configuration.addAllowedOriginPattern("*");
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}