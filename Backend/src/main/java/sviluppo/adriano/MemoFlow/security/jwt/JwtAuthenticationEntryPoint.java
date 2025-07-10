package sviluppo.adriano.MemoFlow.security.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    /**
     * Questo metodo viene chiamato quando un'eccezione AuthenticationException
     * viene lanciata durante un tentativo di accesso a una risorsa protetta.
     * @param request La richiesta HTTP che ha causato l'eccezione.
     * @param response La risposta HTTP da inviare al client.
     * @param authException L'eccezione di autenticazione.
     * @throws IOException Se si verifica un errore di I/O durante la scrittura della risposta.
     * @throws ServletException Se si verifica un errore generico del servlet.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        logger.error("Errore di autenticazione: {}", authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Errore: Non autorizzato");
    }
}