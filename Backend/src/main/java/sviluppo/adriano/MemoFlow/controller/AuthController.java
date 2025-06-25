package sviluppo.adriano.MemoFlow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sviluppo.adriano.MemoFlow.dto.LoginDTO;
import sviluppo.adriano.MemoFlow.entity.Credenziali;
import sviluppo.adriano.MemoFlow.service.AuthService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(
            summary = "Effettua il login dell'utente",
            description = "Autentica l'utente tramite email e password. Se le credenziali sono corrette, restituisce un token (dummy o JWT) e i dati dell'utente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login effettuato con successo"),
            @ApiResponse(responseCode = "401", description = "Credenziali non valide"),
            @ApiResponse(responseCode = "400", description = "Dati della richiesta non validi")
    })

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        Optional<Credenziali> result = authService.login(loginDTO.getEmail(), loginDTO.getPassword());

        if (result.isPresent()) {
            Credenziali c = result.get();

            // Prepari la risposta
            Map<String, Object> response = new HashMap<>();
            response.put("token", "dummy-token");
            response.put("user", Map.of(
                    "id", c.getUtente().getId(),
                    "nome", c.getUtente().getNome(),
                    "email", c.getEmail()
            ));

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenziali errate");
    }
}
