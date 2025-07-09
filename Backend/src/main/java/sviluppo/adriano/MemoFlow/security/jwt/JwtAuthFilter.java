// File: sviluppo/adriano/MemoFlow/security/jwt/JwtAuthFilter.java
package sviluppo.adriano.MemoFlow.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import sviluppo.adriano.MemoFlow.security.service.UserDetailServiceImpl;

import java.io.IOException;

/**
 * Filtro JWT personalizzato che si estende da OncePerRequestFilter.
 * Questo filtro viene eseguito una volta per ogni richiesta HTTP
 * e si occupa di estrarre, validare il token JWT e impostare l'autenticazione
 * nel contesto di sicurezza di Spring.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil; // Il tuo JwtUtil per la gestione del token

    @Autowired
    private UserDetailServiceImpl userDetailServiceImpl; // Il tuo servizio per caricare i dettagli utente

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    /**
     * Questo metodo contiene la logica del filtro e viene eseguito per ogni richiesta.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 1. Estrai il token JWT dall'header Authorization della richiesta
            String jwt = parseJwt(request);

            // 2. Se il token esiste e valido
            if (jwt != null && jwtUtil.validateJwtToken(jwt)) {
                // 3. Estrai l'username (email) dal token
                String username = jwtUtil.getUserNameFromJwtToken(jwt);

                // 4. Carica i dettagli dell'utente usando il CustomUserDetailsService
                UserDetails userDetails = userDetailServiceImpl.loadUserByUsername(username);

                // 5. Crea un oggetto di autenticazione per Spring Security
                // Questo oggetto contiene l'utente (UserDetails), le sue credenziali (password, ma per JWT non necessaria)
                // e le sue autorità (ruoli).
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, // Le credenziali (password) non sono necessarie una volta che il token è valido
                                userDetails.getAuthorities()); // I ruoli dell'utente

                // 6. Imposta i dettagli della richiesta (IP, sessione, ecc.) per l'autenticazione
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 7. Imposta l'oggetto di autenticazione nel SecurityContextHolder di Spring Security.
                // Questo rende l'utente autenticato accessibile in tutta l'applicazione per la durata della richiesta.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Non è stato possibile impostare l'autenticazione dell'utente: {}", e.getMessage());
        }

        // Continua la catena dei filtri di Spring Security
        filterChain.doFilter(request, response);
    }

    /**
     * Metodo helper per estrarre il token JWT dall'header "Authorization".
     * Il token è solitamente nel formato "Bearer <token>".
     * @param request La richiesta HTTP.
     * @return La stringa del token JWT o null se non presente/non valido.
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Rimuovi "Bearer "
        }
        return null;
    }
}