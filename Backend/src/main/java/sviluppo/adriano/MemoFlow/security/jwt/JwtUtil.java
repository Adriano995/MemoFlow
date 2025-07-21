package sviluppo.adriano.MemoFlow.security.jwt;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.ms}")
    private int jwtExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Genera un token JWT per l'utente autenticato.
     * @param authentication L'oggetto Authentication di Spring Security contenente i dettagli dell'utente.
     * @return Il token JWT generato.
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername())) 
                .claim("roles", roles) 
                .setIssuedAt(new Date()) 
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) 
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) 
                .compact(); 
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
                .getSubject();
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
        } catch (SignatureException e) {
            logger.error("Firma JWT non valida: {}", e.getMessage());
        }

        return false;
    }
}