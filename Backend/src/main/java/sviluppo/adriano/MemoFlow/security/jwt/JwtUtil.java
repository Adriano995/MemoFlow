// File: sviluppo/adriano/MemoFlow/security/jwt/JwtUtil.java
package sviluppo.adriano.MemoFlow.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe di utilità per la gestione dei JSON Web Tokens (JWT).
 * Si occupa della generazione, validazione e parsing dei token.
 */
@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    // Chiave segreta per la firma del token, letta dalle proprietà dell'applicazione (es. application.properties)
    // DEVE essere sufficientemente lunga (es. 256 bit = 32 caratteri casuali)
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Durata di validità del token JWT in millisecondi (es. 24 ore)
    @Value("${jwt.expiration.ms}")
    private int jwtExpirationMs;

    // Metodo per generare la chiave segreta (da stringa base64)
    private SecretKey getSigningKey() {
        // Decodifica la stringa base64 della chiave segreta in un array di byte
        // e crea un oggetto SecretKey.
        // Questo è il modo raccomandato da jjwt per gestire le chiavi.
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Genera un token JWT per l'utente autenticato.
     * @param authentication L'oggetto Authentication di Spring Security contenente i dettagli dell'utente.
     * @return Il token JWT generato.
     */
    public String generateJwtToken(Authentication authentication) {
        // L'oggetto UserDetails è il rappresentante dell'utente autenticato in Spring Security.
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        // Estrai i ruoli (Authorities) dell'utente e convertili in una lista di stringhe.
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername())) // Soggetto del token (solitamente l'email o l'ID utente)
                .claim("roles", roles) // Aggiungi i ruoli come "claim" personalizzato nel token
                .setIssuedAt(new Date()) // Data di emissione del token
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Data di scadenza del token
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Firma il token con la chiave segreta e algoritmo HS256
                .compact(); // Costruisci il token come stringa compatta
    }

    /**
     * Estrae il nome utente (subject) dal token JWT.
     * @param token Il token JWT.
     * @return Il nome utente (solitamente l'email).
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // Recupera il soggetto del token
    }

    /**
     * Valida il token JWT.
     * Verifica la firma e la validità del token (es. non scaduto, firma corretta).
     * @param authToken Il token JWT da validare.
     * @return true se il token è valido, false altrimenti.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Token JWT non valido: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT scaduto: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT non supportato: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("La stringa del JWT è vuota: {}", e.getMessage());
        } catch (SignatureException e) { // Aggiunto per gestire errori di firma
            logger.error("Firma JWT non valida: {}", e.getMessage());
        }

        return false;
    }
}