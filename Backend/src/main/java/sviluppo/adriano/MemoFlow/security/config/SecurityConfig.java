// File: sviluppo/adriano/MemoFlow/security/config/SecurityConfig.java
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import sviluppo.adriano.MemoFlow.repository.AuthorityRepository;
import sviluppo.adriano.MemoFlow.security.jwt.JwtAuthFilter; // Assicurati che il nome sia corretto
import sviluppo.adriano.MemoFlow.security.jwt.JwtAuthenticationEntryPoint; // Usato per List.of() o Arrays.asList()
import sviluppo.adriano.MemoFlow.security.service.UserDetailServiceImpl;

/**
 * Classe di configurazione principale per Spring Security.
 * Definisce la catena di filtri di sicurezza, le regole di autorizzazione,
 * il gestore di autenticazione e il password encoder.
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true) // Abilita la sicurezza a livello di metodo (es. @PreAuthorize)
public class SecurityConfig {

    private final UserDetailServiceImpl userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final AuthorityRepository authorityRepository;

    // Se hai creato PasswordEncoderConfig.java separata e vuoi usarla,
    // dovresti iniettarla qui e poi usare il suo metodo per ottenere il PasswordEncoder.
    // Esempio: private final PasswordEncoderConfig passwordEncoderConfig;
    // Esempio nel costruttore: ..., PasswordEncoderConfig passwordEncoderConfig) { this.passwordEncoderConfig = passwordEncoderConfig; }
    // Esempio nel passwordEncoder() bean: return passwordEncoderConfig.passwordEncoder();

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
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Definisce il provider di autenticazione.
     * Utilizza il CustomUserDetailsService per caricare i dettagli dell'utente
     * e il PasswordEncoder per verificare le password.
     * @return Il DaoAuthenticationProvider configurato.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Il tuo CustomUserDetailsService
        authProvider.setPasswordEncoder(passwordEncoder());     // Il tuo PasswordEncoder
        return authProvider;
    }

    /**
     * Definisce il bean AuthenticationManager.
     * Viene usato nel AuthController per autenticare l'utente.
     * @param authConfig L'oggetto di configurazione dell'autenticazione.
     * @return L'AuthenticationManager.
     * @throws Exception Se si verifica un errore durante la configurazione.
     */
    @Bean
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
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .cors(cors -> cors.configurationSource(corsConfigurationSource())) // <--- MODIFICA QUI
                // Disabilita la protezione CSRF (necessario per API REST stateless con JWT)
                .csrf(AbstractHttpConfigurer::disable)
                // Configura la gestione delle eccezioni di autenticazione
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                // Imposta la politica di creazione della sessione a STATELESS
                // (ogni richiesta deve includere il token JWT, non ci sono sessioni lato server)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configura le regole di autorizzazione per le richieste HTTP
                .authorizeHttpRequests(auth -> auth
                        // Permetti l'accesso a questi endpoint senza autenticazione
                        // Endpoint di autenticazione e registrazione
                        .requestMatchers("/auth/**").permitAll()
                        // Swagger UI e documentazione API
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
                        // Esempio: Permetti la creazione di un utente senza autenticazione (registrazione)
                        .requestMatchers(HttpMethod.POST, "/utente/creaUtente").permitAll()
                        .requestMatchers(HttpMethod.GET,"/utente/listaUtenti").hasRole("DEVELOPER")
                        .requestMatchers(HttpMethod.PUT, "/utente/aggiornaDati/{id}").hasAnyRole("DEVELOPER", "USER")
                        .requestMatchers(HttpMethod.PUT, "/utente/cambiaRuoli/{id}").hasAnyRole("DEVELOPER", "PROPRIETARIO_GRUPPO")
                        // Esempio: Endpoint pubblici per le note (es. lista pubblica, se esiste)
                        // ATTENZIONE: Questo rende le note accessibili a tutti.
                        // Se vuoi proteggerle, rimuovi queste righe o specifica ruoli.
                        .requestMatchers(HttpMethod.GET, "/nota/listaTutte").permitAll()
                        // Se vuoi proteggere tutti gli altri endpoint delle note, rimuovi i .permitAll() successivi
                        // e lascia anyRequest().authenticated() per proteggerli.
                        // Esempio: per proteggere /nota/{id}, /nota/eliminaNota, ecc.
                        // Se non vuoi proteggerli, lasciali come .permitAll()
                        .requestMatchers(HttpMethod.GET, "/nota/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/nota/perDataEUtente").permitAll()
                        .requestMatchers(HttpMethod.GET, "/nota/perUtente").permitAll() // Aggiunto da NotaController
                        .requestMatchers("/nota/**").authenticated()
                        // Tutte le altre richieste richiedono autenticazione
                        .anyRequest().authenticated()
                );

        // Collega il tuo authenticationProvider alla catena di sicurezza
        http.authenticationProvider(authenticationProvider());
        // Aggiungi il JwtAuthFilter prima del filtro UsernamePasswordAuthenticationFilter di Spring Security
        // Questo è FONDAMENTALE per il funzionamento del JWT!
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configurazione CORS per permettere richieste da domini diversi.
     * Questo bean è essenziale se il tuo frontend (Angular) e backend girano su porte diverse.
     * @return Il CorsConfigurationSource.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permetti richieste da localhost:4200 (il tuo frontend Angular di default)
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        // Permetti tutti i metodi HTTP che userai
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Permetti tutti gli header
        configuration.setAllowedHeaders(List.of("*"));
        // Permetti l'invio di credenziali (cookie, header di autenticazione)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Applica la configurazione a tutti i percorsi
        return source;
    }
}